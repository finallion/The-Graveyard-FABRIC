package com.lion.graveyard.item;

import com.lion.graveyard.entities.GhoulingBindings;
import com.lion.graveyard.entities.GhoulingEntity;
import com.lion.graveyard.entities.GraveyardMinionEntity;
import com.lion.graveyard.init.TGEntities;
import com.lion.graveyard.init.TGParticles;
import com.lion.graveyard.init.TGSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtTagSizeTracker;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

/// The stack NBT and GhoulingBindings both store the bond.
public class BoneStaffItem extends Item {
    private static final double FETCH_DISTANCE_SQ = 24 * 24;
    private static final int USE_COOLDOWN_TICKS = 10;

    private static final ChunkTicketType<UUID> FETCH_TICKET =
            ChunkTicketType.create("graveyard_ghouling_fetch", UUID::compareTo, 60);

    private static final String CALL_KEY = "FetchCall";

    private static final String MISSES_KEY = "FetchMisses";
    private static final int MAX_FETCH_MISSES = 5;

    public static final String BOND_KEY = "BondId";
    private static final int MAX_LANDING_DROP = 8;

    private static final int CALL_TIMEOUT = 200;
    private static final double ITEM_AIM_DISTANCE = 24.0D;
    private static final double ITEM_AIM_MARGIN = 0.3D;

    private static final String STORED_KEY = "StoredGhouling";
    private static final int STORE_TICKS = 30;
    private static final double STORE_RANGE = 12.0D;
    private static final double STORE_RANGE_SQ = STORE_RANGE * STORE_RANGE;
    private static final int DRAW_SOUND_DELAY = 4;
    private static final int SIT_CLICK_TICKS = 8;
    /// Client NBT limit per stack. The margin leaves room for other staff keys.
    private static final long MAX_STORED_NBT = 1_500_000L;

    private static final Map<UUID, UUID> DRAWING = new HashMap<>();

    private final byte ghoulVariant;

    public BoneStaffItem(byte ghoulVariant) {
        super(new Item.Settings().maxCount(1));
        this.ghoulVariant = ghoulVariant;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();
        ItemStack stack = context.getStack();

        if (player != null && world.isClient) {
            NbtCompound nbt = stack.getNbt();
            if (nbt != null && nbt.containsUuid("OwnerUUID") && !nbt.getUuid("OwnerUUID").equals(player.getUuid())) {
                return ActionResult.FAIL;
            }
            if (isUnbound(stack) || isStored(stack)) {
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        }

        if (player != null && world instanceof ServerWorld serverWorld) {
            NbtCompound nbt = stack.getNbt();
            if (nbt != null && nbt.containsUuid("OwnerUUID") && !nbt.getUuid("OwnerUUID").equals(player.getUuid())) {
                player.sendMessage(Text.translatable("entity.graveyard.ghouling.obey"), true);
                return ActionResult.FAIL;
            }
            BlockPos spawnPos = spawnPosition(world, blockPos, context.getSide());
            if (nbt != null && nbt.contains(STORED_KEY)) {
                release(serverWorld, player, stack, spawnPos);
                player.getItemCooldownManager().set(this, USE_COOLDOWN_TICKS);
                return ActionResult.SUCCESS;
            }
            if (nbt != null && nbt.containsUuid("GhoulingUUID") && isAlive(serverWorld, nbt.getUuid("GhoulingUUID"))) {
                if (!(findLoaded(serverWorld, nbt.getUuid("GhoulingUUID")) instanceof GhoulingEntity bound)
                        || !rejectsStaff(bound, stack, player)) {
                    return ActionResult.PASS;
                }
                nbt = stack.getNbt();
            }

            GhoulingEntity ghouling = TGEntities.GHOULING.get().create(world);
            ghouling.refreshPositionAndAngles(spawnPos, 0.0F, 0.0F);
            ghouling.setOwner(player);
            ghouling.setVariant(ghoulVariant);

            UUID bondId = UUID.randomUUID();
            ghouling.setBondId(bondId);
            stack.getOrCreateNbt().putUuid("GhoulingUUID", ghouling.getUuid());
            stack.getNbt().putUuid(BOND_KEY, bondId);
            if (!stack.getNbt().containsUuid("OwnerUUID")) {
                stack.getNbt().putUuid("OwnerUUID", player.getUuid());
                player.sendMessage(Text.translatable("entity.graveyard.ghouling.spawn"), true);
            } else {
                player.sendMessage(Text.translatable("entity.graveyard.ghouling.respawn"), true);
            }

            ghouling.setStaff(stack);
            ghouling.onSummoned();
            world.spawnEntity(ghouling);
            GhoulingBindings.get(serverWorld).update(ghouling);
            player.getItemCooldownManager().set(this, USE_COOLDOWN_TICKS);
            return ActionResult.SUCCESS;
        }

        return super.useOnBlock(context);
    }

    private static BlockPos spawnPosition(World world, BlockPos clicked, Direction side) {
        return world.getBlockState(clicked).getCollisionShape(world, clicked).isEmpty() ? clicked : clicked.offset(side);
    }

    private static Vec3d clipAtBlocks(PlayerEntity user, Vec3d start, Vec3d end) {
        HitResult block = user.getWorld().raycast(new RaycastContext(start, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, user));
        return block.getType() == HitResult.Type.MISS ? end : block.getPos();
    }

    private static boolean isStored(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        return nbt != null && nbt.contains(STORED_KEY);
    }

    public static boolean isBoundTo(ItemStack stack, GhoulingEntity ghouling) {
        NbtCompound nbt = stack.getNbt();
        return nbt != null && nbt.containsUuid("GhoulingUUID") && nbt.getUuid("GhoulingUUID").equals(ghouling.getUuid());
    }

    private static final SoundEvent STORE_DONE_SOUND = SoundEvents.PARTICLE_SOUL_ESCAPE;

    public static void startDrawing(PlayerEntity player, GhoulingEntity ghouling) {
        DRAWING.put(player.getUuid(), ghouling.getUuid());
    }

    private static void stopDrawSound(ServerWorld world, Entity source) {
        StopSoundS2CPacket packet = new StopSoundS2CPacket(TGSounds.GHOULING_SOUL_DRAW.get().getId(), SoundCategory.PLAYERS);
        for (ServerPlayerEntity listener : world.getPlayers(p -> p.squaredDistanceTo(source) < 32 * 32)) {
            listener.networkHandler.sendPacket(packet);
        }
    }

    private static void streamSouls(ServerWorld world, PlayerEntity player, GhoulingEntity ghouling, int usedTicks) {
        Vec3d from = new Vec3d(ghouling.getX(), ghouling.getY() + ghouling.getHeight() * 0.5D, ghouling.getZ());
        Vec3d side = player.getRotationVec(1.0F).crossProduct(new Vec3d(0.0D, 1.0D, 0.0D)).normalize();
        double handSide = player.getActiveHand() == Hand.MAIN_HAND ? -0.35D : 0.35D;
        Vec3d to = player.getEyePos().subtract(0.0D, 0.4D, 0.0D).add(side.multiply(handSide)).add(player.getRotationVec(1.0F).multiply(0.4D));
        int count = 2 + usedTicks * 6 / STORE_TICKS;
        for (int i = 0; i < count; i++) {
            Vec3d p = from.lerp(to, world.random.nextDouble());
            world.spawnParticles(TGParticles.GRAVEYARD_SOUL_PARTICLE, p.x, p.y, p.z, 1, 0.1D, 0.1D, 0.1D, 0.0D);
            if (i % 2 == 0) {
                world.spawnParticles(ParticleTypes.SOUL, p.x, p.y, p.z, 1, 0.05D, 0.05D, 0.05D, 0.0D);
            }
        }
    }

    private static void release(ServerWorld world, PlayerEntity player, ItemStack stack, BlockPos spawnPos) {
        NbtCompound nbt = stack.getNbt();
        NbtCompound saved = nbt.getCompound(STORED_KEY);
        if (EntityType.getEntityFromNbt(saved, world).orElse(null) instanceof GhoulingEntity ghouling) {
            ghouling.refreshPositionAndAngles(spawnPos, 0.0F, 0.0F);
            ghouling.setSitting(false);
            ItemStack staff = stack.copy();
            staff.getNbt().remove(STORED_KEY);
            ghouling.setStaff(staff);
            if (world.spawnEntity(ghouling)) {
                ghouling.onSummoned();
                nbt.remove(STORED_KEY);
                GhoulingBindings.get(world).update(ghouling);
                player.sendMessage(Text.translatable("entity.graveyard.ghouling.released"), true);
                return;
            }
        }
        player.sendMessage(Text.translatable("entity.graveyard.ghouling.release_failed"), true);
    }

    private static void store(ServerWorld world, PlayerEntity player, ItemStack stack, GhoulingEntity ghouling) {
        if (!fitsInStack(ghouling)) {
            stopDrawSound(world, player);
            player.sendMessage(Text.translatable("entity.graveyard.ghouling.too_heavy"), true);
            return;
        }
        world.playSound(null, ghouling.getBlockPos(), STORE_DONE_SOUND, SoundCategory.PLAYERS, 2.0F, 1.0F);
        stack.getOrCreateNbt().put(STORED_KEY, ghouling.removeForStorage());
        player.getItemCooldownManager().set(stack.getItem(), 20);
        player.sendMessage(Text.translatable("entity.graveyard.ghouling.stored"), true);
    }

    private static boolean fitsInStack(GhoulingEntity ghouling) {
        NbtCompound probe = new NbtCompound();
        ghouling.saveSelfNbt(probe);
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            NbtIo.write(probe, new DataOutputStream(bytes));
            NbtIo.read(new DataInputStream(new ByteArrayInputStream(bytes.toByteArray())), new NbtTagSizeTracker(MAX_STORED_NBT));
            return true;
        } catch (IOException | RuntimeException e) {
            return false;
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPEAR;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!(world instanceof ServerWorld serverWorld) || !(user instanceof PlayerEntity player)) {
            return;
        }
        UUID drawn = DRAWING.get(player.getUuid());
        if (drawn == null) {
            return;
        }
        if (!(findLoaded(serverWorld, drawn) instanceof GhoulingEntity ghouling) || !ghouling.isAlive()
                || ghouling.squaredDistanceTo(player) > STORE_RANGE_SQ || !ghouling.isOwner(player)
                || !isBoundTo(stack, ghouling) || isStored(stack) || !ghouling.acceptsStaff(stack)) {
            DRAWING.remove(player.getUuid());
            stopDrawSound(serverWorld, player);
            user.stopUsingItem();
            return;
        }
        int used = getMaxUseTime(stack) - remainingUseTicks;
        streamSouls(serverWorld, player, ghouling, used);
        if (used == DRAW_SOUND_DELAY) {
            serverWorld.playSound(null, ghouling.getBlockPos(), TGSounds.GHOULING_SOUL_DRAW.get(), SoundCategory.PLAYERS, 1.0F, 1.0F);
        }
        // A stopped use lets a held key use the staff on the ground.
        if (used >= STORE_TICKS) {
            DRAWING.remove(player.getUuid());
            store(serverWorld, player, stack, ghouling);
        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(world instanceof ServerWorld serverWorld) || !(user instanceof PlayerEntity player)) {
            return;
        }
        UUID drawn = DRAWING.remove(player.getUuid());
        if (drawn != null) {
            stopDrawSound(serverWorld, player);
        }
        if (drawn != null && getMaxUseTime(stack) - remainingUseTicks < SIT_CLICK_TICKS
                && findLoaded(serverWorld, drawn) instanceof GhoulingEntity ghouling && ghouling.isAlive()
                && !rejectsStaff(ghouling, stack, player)) {
            ghouling.toggleSitting(player);
        }
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return isStored(stack) || super.hasGlint(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        NbtCompound nbt = stack.getNbt();
        if (nbt != null && nbt.contains(STORED_KEY)) {
            NbtCompound saved = nbt.getCompound(STORED_KEY);
            Text name = null;
            if (saved.contains("CustomName", 8)) {
                try {
                    name = Text.Serializer.fromJson(saved.getString("CustomName"));
                } catch (RuntimeException e) {
                    name = null;
                }
            }
            if (name == null) {
                name = TGEntities.GHOULING.get().getName();
            }
            tooltip.add(Text.translatable("item.graveyard.bone_staff.stored", name));
        }
        super.appendTooltip(stack, world, tooltip, context);
    }

    private static Entity findLoaded(ServerWorld world, UUID id) {
        for (ServerWorld other : world.getServer().getWorlds()) {
            Entity entity = other.getEntity(id);
            if (entity != null) {
                return entity;
            }
        }
        return null;
    }

    public static boolean isUnbound(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        return nbt == null || !nbt.containsUuid("GhoulingUUID");
    }

    public static void rebind(ItemStack stack, PlayerEntity player, GhoulingEntity ghouling) {
        UUID bondId = UUID.randomUUID();
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putUuid("OwnerUUID", player.getUuid());
        nbt.putUuid("GhoulingUUID", ghouling.getUuid());
        nbt.putUuid(BOND_KEY, bondId);
        nbt.remove(CALL_KEY);
        ghouling.setBondId(bondId);
        ghouling.setStaff(stack.copy());
        if (ghouling.getWorld() instanceof ServerWorld serverWorld) {
            GhoulingBindings.get(serverWorld).update(ghouling);
        }
        player.sendMessage(Text.translatable("entity.graveyard.ghouling.rebound"), true);
    }

    public static boolean rejectsStaff(GhoulingEntity ghouling, ItemStack stack, PlayerEntity player) {
        NbtCompound nbt = stack.getNbt();
        if (nbt != null && nbt.containsUuid("GhoulingUUID") && !nbt.getUuid("GhoulingUUID").equals(ghouling.getUuid())) {
            return false;
        }
        if (ghouling.acceptsStaff(stack)) {
            return false;
        }
        if (nbt != null) {
            nbt.remove("OwnerUUID");
            nbt.remove("GhoulingUUID");
            nbt.remove(BOND_KEY);
            nbt.remove(CALL_KEY);
        }
        player.sendMessage(Text.translatable("entity.graveyard.ghouling.forgotten"), true);
        return true;
    }

    private static boolean ownerAirborne(PlayerEntity player) {
        return player.isFallFlying() || player.getAbilities().flying || !player.isOnGround() || player.hasVehicle() || player.isTouchingWater();
    }

    private static boolean isOwnedBy(GhoulingEntity ghouling, PlayerEntity player) {
        return ghouling.getOwnerUuid() == null || ghouling.getOwnerUuid().equals(player.getUuid());
    }

    private static boolean isAlive(ServerWorld world, UUID ghouling) {
        for (ServerWorld other : world.getServer().getWorlds()) {
            Entity entity = other.getEntity(ghouling);
            if (entity != null) {
                return entity.isAlive();
            }
        }
        return GhoulingBindings.get(world).get(ghouling) != null;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!(world instanceof ServerWorld serverWorld) || !(entity instanceof PlayerEntity player)) {
            return;
        }
        NbtCompound nbt = stack.getNbt();
        if (nbt == null || nbt.contains(STORED_KEY) || !nbt.containsUuid("GhoulingUUID") || !nbt.containsUuid("OwnerUUID")
                || !nbt.getUuid("OwnerUUID").equals(player.getUuid())) {
            return;
        }

        boolean calling = nbt.getLong(CALL_KEY) > 0L;
        if (calling && world.getTime() > nbt.getLong(CALL_KEY)) {
            nbt.remove(CALL_KEY);
            player.sendMessage(Text.translatable("entity.graveyard.ghouling.lost"), true);
            calling = false;
        }
        if (!calling && (world.getTime() + player.getId()) % GhoulingEntity.BINDING_INTERVAL != 0) {
            return;
        }

        UUID ghoulingId = nbt.getUuid("GhoulingUUID");
        GhoulingBindings.Binding binding = GhoulingBindings.get(serverWorld).get(ghoulingId);
        if (binding == null) {
            nbt.remove(CALL_KEY);
            return;
        }
        if (!calling && binding.sitting()) {
            return;
        }

        ServerWorld home = serverWorld.getServer().getWorld(binding.dimension());
        if (home == null) {
            nbt.remove(CALL_KEY);
            return;
        }

        GhoulingEntity ghouling = home.getEntity(ghoulingId) instanceof GhoulingEntity found ? found : null;
        if (ghouling == null) {
            int misses = nbt.getInt(MISSES_KEY);
            if (binding.pos() != null && player.getUuid().equals(binding.owner()) && (calling || !ownerAirborne(player))
                    && (calling || misses < MAX_FETCH_MISSES)) {
                home.getChunkManager().addTicket(FETCH_TICKET, new ChunkPos(binding.pos()), 2, ghoulingId);
                if (!calling) {
                    nbt.putInt(MISSES_KEY, misses + 1);
                }
            }
            return;
        }
        nbt.remove(MISSES_KEY);
        if (!isOwnedBy(ghouling, player) || rejectsStaff(ghouling, stack, player)) {
            return;
        }

        if (calling) {
            nbt.remove(CALL_KEY);
            if (!fetch(ghouling, serverWorld, player)) {
                player.sendMessage(Text.translatable("entity.graveyard.ghouling.no_landing"), true);
            }
            return;
        }

        boolean farAway = home != serverWorld || ghouling.squaredDistanceTo(player) > FETCH_DISTANCE_SQ;
        if (farAway && !ownerAirborne(player) && !ghouling.isSitting() && !ghouling.isLeashed() && !ghouling.hasVehicle()) {
            fetch(ghouling, serverWorld, player);
        }
    }

    private static void call(ServerWorld world, PlayerEntity owner, ItemStack stack, UUID ghoulingId) {
        GhoulingBindings bindings = GhoulingBindings.get(world);
        GhoulingBindings.Binding binding = bindings.get(ghoulingId);
        if (binding == null) {
            owner.sendMessage(Text.translatable("entity.graveyard.ghouling.severed"), true);
            return;
        }

        ServerWorld home = world.getServer().getWorld(binding.dimension());
        if (home != null && home.getEntity(ghoulingId) instanceof GhoulingEntity ghouling) {
            if (!isOwnedBy(ghouling, owner) || rejectsStaff(ghouling, stack, owner)) {
                return;
            }
            stack.removeSubNbt(CALL_KEY);
            if (!fetch(ghouling, world, owner)) {
                owner.sendMessage(Text.translatable("entity.graveyard.ghouling.no_landing"), true);
            }
            return;
        }

        // A bond without a last known position cannot load its chunk.
        if (home == null || binding.pos() == null) {
            stack.removeSubNbt(CALL_KEY);
            bindings.remove(ghoulingId);
            owner.sendMessage(Text.translatable("entity.graveyard.ghouling.severed"), true);
            return;
        }

        home.getChunkManager().addTicket(FETCH_TICKET, new ChunkPos(binding.pos()), 2, ghoulingId);
        stack.removeSubNbt(MISSES_KEY);
        stack.getOrCreateNbt().putLong(CALL_KEY, world.getTime() + CALL_TIMEOUT);
        owner.sendMessage(Text.translatable("entity.graveyard.ghouling.call"), true);
    }

    @Nullable
    private static Vec3d findLanding(GhoulingEntity ghouling, ServerWorld world, PlayerEntity player) {
        EntityDimensions size = ghouling.getDimensions(ghouling.getPose());
        Box atOwner = size.getBoxAt(player.getX(), player.getY(), player.getZ());
        if (player.isOnGround() && world.isSpaceEmpty(ghouling, atOwner) && !world.containsFluid(atOwner)) {
            return player.getPos();
        }
        BlockPos top = player.getBlockPos();
        for (int i = 0; i <= MAX_LANDING_DROP; i++) {
            BlockPos feet = top.down(i);
            Box box = size.getBoxAt(player.getX(), feet.getY(), player.getZ());
            if (!world.isSpaceEmpty(ghouling, box) || world.getFluidState(feet).isIn(FluidTags.LAVA)) {
                return null;
            }
            BlockPos floor = feet.down();
            if (world.getBlockState(floor).isSideSolidFullSquare(world, floor, Direction.UP)) {
                return new Vec3d(player.getX(), feet.getY(), player.getZ());
            }
        }
        return null;
    }

    private static boolean fetch(GhoulingEntity ghouling, ServerWorld world, PlayerEntity player) {
        Vec3d landing = findLanding(ghouling, world, player);
        if (landing == null) {
            return false;
        }
        ghouling.setTarget(null);
        ghouling.setAttacking(false);
        ghouling.setSitting(false);
        ghouling.getNavigation().stop();
        ghouling.teleport(world, landing.x, landing.y, landing.z,
                EnumSet.noneOf(PositionFlag.class), ghouling.getYaw(), ghouling.getPitch());
        // A dimension change creates a new entity. Move the timer to the live instance.
        GhoulingEntity arrived = world.getEntity(ghouling.getUuid()) instanceof GhoulingEntity found ? found : ghouling;
        arrived.setTeleportTimer(15);
        arrived.fallDistance = 0.0F;
        GhoulingBindings.get(world).update(arrived);
        return true;
    }

    @Nullable
    private static ItemEntity findItemInSight(PlayerEntity user, @Nullable HitResult livingHit) {
        Vec3d start = user.getEyePos();
        Vec3d end = clipAtBlocks(user, start, start.add(user.getRotationVec(1.0F).multiply(ITEM_AIM_DISTANCE)));
        double limit = livingHit == null ? ITEM_AIM_DISTANCE * ITEM_AIM_DISTANCE : Math.min(ITEM_AIM_DISTANCE * ITEM_AIM_DISTANCE, livingHit.getPos().squaredDistanceTo(start));
        ItemEntity best = null;
        for (ItemEntity item : user.getWorld().getEntitiesByClass(ItemEntity.class, user.getBoundingBox().stretch(end.subtract(start)).expand(1.0D), Entity::isAlive)) {
            var hit = item.getBoundingBox().expand(ITEM_AIM_MARGIN).raycast(start, end);
            if (hit.isPresent()) {
                double distance = hit.get().squaredDistanceTo(start);
                if (distance < limit) {
                    limit = distance;
                    best = item;
                }
            }
        }
        return best;
    }

    private static boolean startFarDraw(World world, PlayerEntity user, Hand hand, ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (user.isSneaking() || isStored(stack) || nbt == null || !nbt.containsUuid("GhoulingUUID")) {
            return false;
        }
        UUID id = nbt.getUuid("GhoulingUUID");
        Vec3d start = user.getEyePos();
        Vec3d end = clipAtBlocks(user, start, start.add(user.getRotationVec(1.0F).multiply(STORE_RANGE)));
        Box area = user.getBoundingBox().stretch(end.subtract(start)).expand(1.0D);
        for (GhoulingEntity ghouling : world.getEntitiesByClass(GhoulingEntity.class, area, e -> e.getUuid().equals(id) && e.isAlive())) {
            if (ghouling.isOwner(user) && ghouling.getSpawnTimer() <= 0 && ghouling.acceptsStaff(stack)
                    && ghouling.getBoundingBox().expand(ghouling.getTargetingMargin()).raycast(start, end).isPresent()) {
                user.setCurrentHand(hand);
                if (!world.isClient) {
                    startDrawing(user, ghouling);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient) {
            user.getItemCooldownManager().set(this, USE_COOLDOWN_TICKS);
        }
        NbtCompound nbt = stack.getNbt();
        boolean owned = nbt != null && nbt.containsUuid("OwnerUUID");
        if (owned && !nbt.getUuid("OwnerUUID").equals(user.getUuid())) {
            if (!world.isClient) {
                user.sendMessage(Text.translatable("entity.graveyard.ghouling.obey"), true);
            }
            return TypedActionResult.fail(stack);
        }
        if (owned && startFarDraw(world, user, hand, stack)) {
            return TypedActionResult.consume(stack);
        }
        if (world.isClient) {
            return owned ? TypedActionResult.success(stack, true) : super.use(world, user, hand);
        }
        if (isStored(stack)) {
            user.sendMessage(Text.translatable("entity.graveyard.ghouling.resting"), true);
            return TypedActionResult.success(stack, false);
        }
        if (nbt != null && nbt.contains("GhoulingUUID") && owned) {
            UUID ghoulingUUID = nbt.getUuid("GhoulingUUID");
            if (user.isSneaking()) {
                if (world instanceof ServerWorld serverWorld) {
                    call(serverWorld, user, stack, ghoulingUUID);
                }
            } else {
                GhoulingEntity ghouling = world.getEntitiesByClass(GhoulingEntity.class, user.getBoundingBox().expand(100), Entity::isAlive).stream().filter(entity -> entity.getUuid().compareTo(ghoulingUUID) == 0).findFirst().orElse(null);
                if (ghouling != null && isOwnedBy(ghouling, user) && !rejectsStaff(ghouling, stack, user)) {
                    Predicate<Entity> predicate = entity -> {
                        if (entity instanceof LivingEntity target && ghouling.canTarget(target) && ghouling.canAttackWithOwner(target, user)) {
                            if (entity instanceof TameableEntity tameableEntity) {
                                if (tameableEntity.getOwnerUuid() != null) {
                                    return tameableEntity.getOwnerUuid().compareTo(nbt.getUuid("OwnerUUID")) != 0;
                                }
                            } else if (entity instanceof GraveyardMinionEntity minion) {
                                if (minion.getOwnerUuid() != null) {
                                    return minion.getOwnerUuid().compareTo(nbt.getUuid("OwnerUUID")) != 0;
                                }
                            }
                            return true;
                        }
                        return false;
                    };
                    int distance = 100;
                    Vec3d start = user.getEyePos();
                    Vec3d end = clipAtBlocks(user, start, start.add(user.getRotationVec(1.0F).multiply(distance)));
                    Vec3d rot = end.subtract(start);
                    Box box = user.getBoundingBox().stretch(rot).expand(1.0D);
                    HitResult result = ProjectileUtil.raycast(user, start, end, box, predicate, distance * distance);
                    ItemEntity targetItem = findItemInSight(user, result);
                    if (targetItem != null) {
                        if (!ghouling.hasCoffin()) {
                            user.sendMessage(Text.translatable("entity.graveyard.ghouling.no_coffin"), true);
                        } else if (ghouling.isCoffinFull()) {
                            user.sendMessage(Text.translatable("entity.graveyard.ghouling.coffin_full"), true);
                        } else {
                            ghouling.setTarget(null);
                            ghouling.setAttacking(false);
                            ghouling.setSitting(false);
                            ghouling.collect(targetItem);
                            user.sendMessage(Text.translatable("entity.graveyard.ghouling.collect"), true);
                        }
                    } else if (result != null && result.getType() == HitResult.Type.ENTITY) {
                        Entity entity = ((EntityHitResult) result).getEntity();
                        if (entity instanceof LivingEntity livingEntity) {
                            user.sendMessage(Text.translatable("entity.graveyard.ghouling.kill"), true);
                            ghouling.playAttackSound = true;
                            ghouling.setTarget(livingEntity);
                            ghouling.setAttacking(true);
                            ghouling.setSitting(false);
                        }
                    }
                }
            }
        }

        return owned ? TypedActionResult.success(stack, false) : super.use(world, user, hand);
    }

}
