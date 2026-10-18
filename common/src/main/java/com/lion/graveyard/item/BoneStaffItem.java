package com.lion.graveyard.item;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.GhoulingBindings;
import com.lion.graveyard.entities.GhoulingEntity;
import com.lion.graveyard.entities.GraveyardMinionEntity;
import com.lion.graveyard.init.TGEntities;
import com.lion.graveyard.init.TGParticles;
import com.lion.graveyard.init.TGSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
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

    private static final TicketType<UUID> FETCH_TICKET =
            TicketType.create("graveyard_ghouling_fetch", UUID::compareTo, 60);

    private static final int MAX_FETCH_MISSES = 5;
    private static final int MAX_LANDING_DROP = 8;

    private static final int CALL_TIMEOUT = 200;
    private static final double ITEM_AIM_DISTANCE = 24.0D;
    private static final double ITEM_AIM_MARGIN = 0.3D;

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
        super(new Item.Properties().stacksTo(1));
        this.ghoulVariant = ghoulVariant;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (player != null && world.isClientSide) {
            UUID owner = LegacyItemData.ownerUuid(stack);

            if (owner != null && !owner.equals(player.getUUID())) {
                return InteractionResult.FAIL;
            }

            if (isUnbound(stack) || LegacyItemData.isStored(stack)) {
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.PASS;
        }

        if (player != null && world instanceof ServerLevel serverWorld) {
            UUID owner = LegacyItemData.ownerUuid(stack);

            if (owner != null && !owner.equals(player.getUUID())) {
                player.displayClientMessage(Component.translatable("entity.graveyard.ghouling.obey"), true);
                return InteractionResult.FAIL;
            }

            BlockPos spawnPos = spawnPosition(world, blockPos, context.getClickedFace());

            if (LegacyItemData.isStored(stack)) {
                release(serverWorld, player, stack, spawnPos);
                player.getCooldowns().addCooldown(this, USE_COOLDOWN_TICKS);
                return InteractionResult.SUCCESS;
            }

            UUID boundId = LegacyItemData.ghoulingUuid(stack);

            if (boundId != null && isAlive(serverWorld, boundId)) {
                if (!(findLoaded(serverWorld, boundId) instanceof GhoulingEntity bound)
                        || !rejectsStaff(bound, stack, player)) {
                    return InteractionResult.PASS;
                }
            }

            GhoulingEntity ghouling = TGEntities.GHOULING.get().create(world);
            ghouling.moveTo(spawnPos, 0.0F, 0.0F);
            ghouling.setOwner(player);
            ghouling.setVariant(ghoulVariant);

            UUID bondId = UUID.randomUUID();
            ghouling.setBondId(bondId);
            boolean firstBond = LegacyItemData.ownerUuid(stack) == null;
            LegacyItemData.edit(stack, data -> {
                data.putUUID(LegacyItemData.GHOULING_UUID, ghouling.getUUID());
                data.putUUID(LegacyItemData.BOND_ID, bondId);

                if (firstBond) {
                    data.putUUID(LegacyItemData.OWNER_UUID, player.getUUID());
                }
            });

            if (firstBond) {
                player.displayClientMessage(Component.translatable("entity.graveyard.ghouling.spawn"), true);
            } else {
                player.displayClientMessage(Component.translatable("entity.graveyard.ghouling.respawn"), true);
            }

            ghouling.setStaff(stack);
            ghouling.prepareSpawn();
            world.addFreshEntity(ghouling);
            ghouling.playSpawnSounds();
            GhoulingBindings.get(serverWorld).update(ghouling);
            player.getCooldowns().addCooldown(this, USE_COOLDOWN_TICKS);
            return InteractionResult.SUCCESS;
        }

        return super.useOn(context);
    }

    private static BlockPos spawnPosition(Level world, BlockPos clicked, Direction side) {
        return world.getBlockState(clicked).getCollisionShape(world, clicked).isEmpty() ? clicked : clicked.relative(side);
    }

    private static Vec3 clipAtBlocks(Player user, Vec3 start, Vec3 end) {
        HitResult block = user.level().clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, user));
        return block.getType() == HitResult.Type.MISS ? end : block.getLocation();
    }

    public static boolean isBoundTo(ItemStack stack, GhoulingEntity ghouling) {
        return ghouling.getUUID().equals(LegacyItemData.ghoulingUuid(stack));
    }

    private static final SoundEvent STORE_DONE_SOUND = SoundEvents.SOUL_ESCAPE.value();

    public static void startDrawing(Player player, GhoulingEntity ghouling) {
        DRAWING.put(player.getUUID(), ghouling.getUUID());
    }

    private static void stopDrawSound(ServerLevel world, Entity source) {
        ClientboundStopSoundPacket packet = new ClientboundStopSoundPacket(TGSounds.GHOULING_SOUL_DRAW.get().getLocation(), SoundSource.PLAYERS);
        for (ServerPlayer listener : world.getPlayers(p -> p.distanceToSqr(source) < 32 * 32)) {
            listener.connection.send(packet);
        }
    }

    private static void streamSouls(ServerLevel world, Player player, GhoulingEntity ghouling, int usedTicks) {
        Vec3 from = new Vec3(ghouling.getX(), ghouling.getY() + ghouling.getBbHeight() * 0.5D, ghouling.getZ());
        Vec3 side = player.getViewVector(1.0F).cross(new Vec3(0.0D, 1.0D, 0.0D)).normalize();
        double handSide = player.getUsedItemHand() == InteractionHand.MAIN_HAND ? -0.35D : 0.35D;
        Vec3 to = player.getEyePosition()
                .subtract(0.0D, 0.4D, 0.0D)
                .add(side.scale(handSide))
                .add(player.getViewVector(1.0F).scale(0.4D));
        int count = 2 + usedTicks * 6 / STORE_TICKS;
        for (int i = 0; i < count; i++) {
            Vec3 p = from.lerp(to, world.random.nextDouble());
            world.sendParticles(TGParticles.GRAVEYARD_SOUL_PARTICLE, p.x, p.y, p.z, 1, 0.1D, 0.1D, 0.1D, 0.0D);

            if (i % 2 == 0) {
                world.sendParticles(ParticleTypes.SOUL, p.x, p.y, p.z, 1, 0.05D, 0.05D, 0.05D, 0.0D);
            }
        }
    }

    private static void release(ServerLevel world, Player player, ItemStack stack, BlockPos spawnPos) {
        CompoundTag saved = LegacyItemData.view(stack).getCompound(LegacyItemData.STORED_GHOULING);

        if (EntityType.create(saved, world).orElse(null) instanceof GhoulingEntity ghouling) {
            ghouling.moveTo(spawnPos, 0.0F, 0.0F);
            ghouling.setSitting(false);
            ItemStack staff = stack.copy();
            LegacyItemData.clearStored(staff);
            ghouling.setStaff(staff);
            ghouling.prepareSpawn();

            if (world.addFreshEntity(ghouling)) {
                ghouling.playSpawnSounds();
                LegacyItemData.clearStored(stack);
                GhoulingBindings.get(world).update(ghouling);
                player.displayClientMessage(Component.translatable("entity.graveyard.ghouling.released"), true);
                return;
            }
        }

        player.displayClientMessage(Component.translatable("entity.graveyard.ghouling.release_failed"), true);
    }

    private static void store(ServerLevel world, Player player, ItemStack stack, GhoulingEntity ghouling) {
        if (!fitsInStack(ghouling)) {
            stopDrawSound(world, player);
            player.displayClientMessage(Component.translatable("entity.graveyard.ghouling.too_heavy"), true);
            return;
        }

        world.playSound(null, ghouling.blockPosition(), STORE_DONE_SOUND, SoundSource.PLAYERS, 2.0F, 1.0F);
        LegacyItemData.store(stack, ghouling.removeForStorage());
        player.getCooldowns().addCooldown(stack.getItem(), 20);
        player.displayClientMessage(Component.translatable("entity.graveyard.ghouling.stored"), true);
    }

    private static boolean fitsInStack(GhoulingEntity ghouling) {
        CompoundTag probe = new CompoundTag();
        ghouling.saveAsPassenger(probe);
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            NbtIo.write(probe, new DataOutputStream(bytes));
            NbtIo.read(new DataInputStream(new ByteArrayInputStream(bytes.toByteArray())), NbtAccounter.create(MAX_STORED_NBT));
            return true;
        } catch (IOException | RuntimeException e) {
            Graveyard.LOGGER.debug("The Ghouling does not fit into the staff stack", e);
            return false;
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.SPEAR;
    }

    @Override
    public void onUseTick(Level world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!(world instanceof ServerLevel serverWorld) || !(user instanceof Player player)) {
            return;
        }

        UUID drawn = DRAWING.get(player.getUUID());

        if (drawn == null) {
            return;
        }

        if (!(findLoaded(serverWorld, drawn) instanceof GhoulingEntity ghouling) || !ghouling.isAlive()
                || ghouling.distanceToSqr(player) > STORE_RANGE_SQ || !ghouling.isOwner(player)
                || !isBoundTo(stack, ghouling) || LegacyItemData.isStored(stack) || !ghouling.acceptsStaff(stack)) {
            DRAWING.remove(player.getUUID());
            stopDrawSound(serverWorld, player);
            user.releaseUsingItem();
            return;
        }

        int used = getUseDuration(stack, user) - remainingUseTicks;
        streamSouls(serverWorld, player, ghouling, used);

        if (used == DRAW_SOUND_DELAY) {
            serverWorld.playSound(null, ghouling.blockPosition(), TGSounds.GHOULING_SOUL_DRAW.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }

        // A stopped use lets a held key use the staff on the ground.
        if (used >= STORE_TICKS) {
            DRAWING.remove(player.getUUID());
            store(serverWorld, player, stack, ghouling);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks) {
        if (!(world instanceof ServerLevel serverWorld) || !(user instanceof Player player)) {
            return;
        }

        UUID drawn = DRAWING.remove(player.getUUID());

        if (drawn != null) {
            stopDrawSound(serverWorld, player);
        }

        if (drawn != null && getUseDuration(stack, user) - remainingUseTicks < SIT_CLICK_TICKS
                && findLoaded(serverWorld, drawn) instanceof GhoulingEntity ghouling && ghouling.isAlive()
                && !rejectsStaff(ghouling, stack, player)) {
            ghouling.toggleSitting(player);
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return LegacyItemData.isStored(stack) || super.isFoil(stack);
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            Item.TooltipContext tooltipContext,
            List<Component> tooltip,
            TooltipFlag context
    ) {
        if (LegacyItemData.isStored(stack)) {
            CompoundTag saved = LegacyItemData.view(stack).getCompound(LegacyItemData.STORED_GHOULING);
            Component name = null;

            if (saved.contains("CustomName", Tag.TAG_STRING)) {
                try {
                    HolderLookup.Provider registries = tooltipContext.registries() == null ? RegistryAccess.EMPTY : tooltipContext.registries();
                    name = Component.Serializer.fromJson(saved.getString("CustomName"), registries);
                } catch (RuntimeException e) {
                    Graveyard.LOGGER.debug("Cannot read the stored Ghouling name", e);
                    name = null;
                }
            }

            if (name == null) {
                name = TGEntities.GHOULING.get().getDescription();
            }

            tooltip.add(Component.translatable("item.graveyard.bone_staff.stored", name));
        }

        super.appendHoverText(stack, tooltipContext, tooltip, context);
    }

    private static Entity findLoaded(ServerLevel world, UUID id) {
        for (ServerLevel other : world.getServer().getAllLevels()) {
            Entity entity = other.getEntity(id);

            if (entity != null) {
                return entity;
            }
        }
        return null;
    }

    public static boolean isUnbound(ItemStack stack) {
        return LegacyItemData.ghoulingUuid(stack) == null;
    }

    public static void rebind(ItemStack stack, Player player, GhoulingEntity ghouling) {
        UUID bondId = UUID.randomUUID();
        LegacyItemData.edit(stack, data -> {
            data.putUUID(LegacyItemData.OWNER_UUID, player.getUUID());
            data.putUUID(LegacyItemData.GHOULING_UUID, ghouling.getUUID());
            data.putUUID(LegacyItemData.BOND_ID, bondId);
            data.remove(LegacyItemData.FETCH_CALL);
        });
        ghouling.setBondId(bondId);
        ghouling.setStaff(stack.copy());

        if (ghouling.level() instanceof ServerLevel serverWorld) {
            GhoulingBindings.get(serverWorld).update(ghouling);
        }

        player.displayClientMessage(Component.translatable("entity.graveyard.ghouling.rebound"), true);
    }

    public static boolean rejectsStaff(GhoulingEntity ghouling, ItemStack stack, Player player) {
        UUID bound = LegacyItemData.ghoulingUuid(stack);

        if (bound != null && !bound.equals(ghouling.getUUID())) {
            return false;
        }

        if (ghouling.acceptsStaff(stack)) {
            return false;
        }

        LegacyItemData.clearBond(stack);

        player.displayClientMessage(Component.translatable("entity.graveyard.ghouling.forgotten"), true);
        return true;
    }

    private static boolean ownerAirborne(Player player) {
        return player.isFallFlying() || player.getAbilities().flying || !player.onGround() || player.isPassenger() || player.isInWater();
    }

    private static boolean isOwnedBy(GhoulingEntity ghouling, Player player) {
        return ghouling.getOwnerUuid() == null || ghouling.getOwnerUuid().equals(player.getUUID());
    }

    private static boolean isAlive(ServerLevel world, UUID ghouling) {
        for (ServerLevel other : world.getServer().getAllLevels()) {
            Entity entity = other.getEntity(ghouling);

            if (entity != null) {
                return entity.isAlive();
            }
        }
        return GhoulingBindings.get(world).get(ghouling) != null;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
        if (!(world instanceof ServerLevel serverWorld) || !(entity instanceof Player player)) {
            return;
        }

        CompoundTag nbt = LegacyItemData.view(stack);

        if (nbt.contains(LegacyItemData.STORED_GHOULING) || !nbt.hasUUID(LegacyItemData.GHOULING_UUID) || !nbt.hasUUID(LegacyItemData.OWNER_UUID)
                || !nbt.getUUID(LegacyItemData.OWNER_UUID).equals(player.getUUID())) {
            return;
        }

        long callDeadline = nbt.getLong(LegacyItemData.FETCH_CALL);
        boolean calling = callDeadline > 0L;

        if (calling && world.getGameTime() > callDeadline) {
            LegacyItemData.clearFetchCall(stack);
            player.displayClientMessage(Component.translatable("entity.graveyard.ghouling.lost"), true);
            calling = false;
        }

        if (!calling && (world.getGameTime() + player.getId()) % GhoulingEntity.BINDING_INTERVAL != 0) {
            return;
        }

        UUID ghoulingId = nbt.getUUID(LegacyItemData.GHOULING_UUID);
        GhoulingBindings.Binding binding = GhoulingBindings.get(serverWorld).get(ghoulingId);

        if (binding == null) {
            LegacyItemData.clearFetchCall(stack);
            return;
        }

        if (!calling && binding.sitting()) {
            return;
        }

        ServerLevel home = serverWorld.getServer().getLevel(binding.dimension());

        if (home == null) {
            LegacyItemData.clearFetchCall(stack);
            return;
        }

        GhoulingEntity ghouling = home.getEntity(ghoulingId) instanceof GhoulingEntity found ? found : null;

        if (ghouling == null) {
            int misses = LegacyItemData.fetchMisses(stack);

            if (binding.pos() != null && player.getUUID().equals(binding.owner()) && (calling || !ownerAirborne(player))
                    && (calling || misses < MAX_FETCH_MISSES)) {
                home.getChunkSource().addRegionTicket(FETCH_TICKET, new ChunkPos(binding.pos()), 2, ghoulingId);

                if (!calling) {
                    LegacyItemData.edit(stack, data -> data.putInt(LegacyItemData.FETCH_MISSES, misses + 1));

                    if (misses + 1 == MAX_FETCH_MISSES) {
                        player.displayClientMessage(Component.translatable("entity.graveyard.ghouling.gave_up"), true);
                    }
                }
            }

            return;
        }

        LegacyItemData.clearFetchMisses(stack);

        if (!isOwnedBy(ghouling, player) || rejectsStaff(ghouling, stack, player)) {
            return;
        }

        if (calling) {
            LegacyItemData.clearFetchCall(stack);

            if (!fetch(ghouling, serverWorld, player)) {
                player.displayClientMessage(Component.translatable("entity.graveyard.ghouling.no_landing"), true);
            }

            return;
        }

        boolean farAway = home != serverWorld || ghouling.distanceToSqr(player) > FETCH_DISTANCE_SQ;

        if (farAway && !ownerAirborne(player) && !ghouling.isSitting() && !ghouling.isLeashed() && !ghouling.isPassenger()) {
            fetch(ghouling, serverWorld, player);
        }
    }

    private static void call(ServerLevel world, Player owner, ItemStack stack, UUID ghoulingId) {
        GhoulingBindings.Binding binding = GhoulingBindings.get(world).get(ghoulingId);

        if (binding == null) {
            owner.displayClientMessage(Component.translatable("entity.graveyard.ghouling.severed"), true);
            return;
        }

        if (findLoaded(world, ghoulingId) instanceof GhoulingEntity ghouling) {
            if (!isOwnedBy(ghouling, owner) || rejectsStaff(ghouling, stack, owner)) {
                return;
            }

            LegacyItemData.clearFetchCall(stack);

            if (!fetch(ghouling, world, owner)) {
                owner.displayClientMessage(Component.translatable("entity.graveyard.ghouling.no_landing"), true);
            }

            return;
        }

        ServerLevel home = world.getServer().getLevel(binding.dimension());

        if (home == null || binding.pos() == null) {
            LegacyItemData.clearFetchCall(stack);
            owner.displayClientMessage(Component.translatable("entity.graveyard.ghouling.not_found"), true);
            return;
        }

        home.getChunkSource().addRegionTicket(FETCH_TICKET, new ChunkPos(binding.pos()), 2, ghoulingId);
        long callDeadline = world.getGameTime() + CALL_TIMEOUT;
        LegacyItemData.clearFetchMisses(stack);
        LegacyItemData.edit(stack, data -> data.putLong(LegacyItemData.FETCH_CALL, callDeadline));
        owner.displayClientMessage(Component.translatable("entity.graveyard.ghouling.call"), true);
    }

    @Nullable
    private static Vec3 findLanding(GhoulingEntity ghouling, ServerLevel world, Player player) {
        EntityDimensions size = ghouling.getDimensions(ghouling.getPose());
        AABB atOwner = size.makeBoundingBox(player.getX(), player.getY(), player.getZ());

        if (player.onGround() && world.noCollision(ghouling, atOwner) && !world.containsAnyLiquid(atOwner)) {
            return player.position();
        }

        BlockPos top = player.blockPosition();
        for (int i = 0; i <= MAX_LANDING_DROP; i++) {
            BlockPos feet = top.below(i);
            AABB box = size.makeBoundingBox(player.getX(), feet.getY(), player.getZ());

            if (!world.noCollision(ghouling, box) || world.getFluidState(feet).is(FluidTags.LAVA)) {
                return null;
            }

            BlockPos floor = feet.below();

            if (world.getBlockState(floor).isFaceSturdy(world, floor, Direction.UP)) {
                return new Vec3(player.getX(), feet.getY(), player.getZ());
            }
        }
        return null;
    }

    private static boolean fetch(GhoulingEntity ghouling, ServerLevel world, Player player) {
        Vec3 landing = findLanding(ghouling, world, player);

        if (landing == null) {
            return false;
        }

        ghouling.setTarget(null);
        ghouling.setAggressive(false);
        ghouling.setSitting(false);
        ghouling.getNavigation().stop();
        ghouling.teleportTo(
                world,
                landing.x,
                landing.y,
                landing.z,
                EnumSet.noneOf(RelativeMovement.class),
                ghouling.getYRot(),
                ghouling.getXRot()
        );
        // A dimension change creates a new entity. Move the timer to the live instance.
        GhoulingEntity arrived = world.getEntity(ghouling.getUUID()) instanceof GhoulingEntity found ? found : ghouling;
        arrived.setTeleportTimer(15);
        arrived.fallDistance = 0.0F;
        GhoulingBindings.get(world).update(arrived);
        return true;
    }

    @Nullable
    private static ItemEntity findItemInSight(Player user, @Nullable HitResult livingHit) {
        Vec3 start = user.getEyePosition();
        Vec3 end = clipAtBlocks(user, start, start.add(user.getViewVector(1.0F).scale(ITEM_AIM_DISTANCE)));
        double limit = livingHit == null ? ITEM_AIM_DISTANCE * ITEM_AIM_DISTANCE : Math.min(ITEM_AIM_DISTANCE * ITEM_AIM_DISTANCE, livingHit.getLocation().distanceToSqr(start));
        ItemEntity best = null;
        for (ItemEntity item : user.level().getEntitiesOfClass(ItemEntity.class, user.getBoundingBox().expandTowards(end.subtract(start)).inflate(1.0D), Entity::isAlive)) {
            var hit = item.getBoundingBox().inflate(ITEM_AIM_MARGIN).clip(start, end);

            if (hit.isPresent()) {
                double distance = hit.get().distanceToSqr(start);

                if (distance < limit) {
                    limit = distance;
                    best = item;
                }
            }
        }
        return best;
    }

    private static boolean startFarDraw(Level world, Player user, InteractionHand hand, ItemStack stack) {
        UUID id = LegacyItemData.ghoulingUuid(stack);

        if (user.isShiftKeyDown() || LegacyItemData.isStored(stack) || id == null) {
            return false;
        }

        Vec3 start = user.getEyePosition();
        Vec3 end = clipAtBlocks(user, start, start.add(user.getViewVector(1.0F).scale(STORE_RANGE)));
        AABB area = user.getBoundingBox().expandTowards(end.subtract(start)).inflate(1.0D);
        for (GhoulingEntity ghouling : world.getEntitiesOfClass(GhoulingEntity.class, area, e -> e.getUUID().equals(id) && e.isAlive())) {
            if (ghouling.isOwner(user) && ghouling.getSpawnTimer() <= 0 && ghouling.acceptsStaff(stack)
                    && ghouling.getBoundingBox().inflate(ghouling.getPickRadius()).clip(start, end).isPresent()) {
                user.startUsingItem(hand);

                if (!world.isClientSide) {
                    startDrawing(user, ghouling);
                }

                return true;
            }
        }
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);

        if (!world.isClientSide) {
            user.getCooldowns().addCooldown(this, USE_COOLDOWN_TICKS);
        }

        UUID ownerId = LegacyItemData.ownerUuid(stack);
        boolean owned = ownerId != null;

        if (owned && !ownerId.equals(user.getUUID())) {
            if (!world.isClientSide) {
                user.displayClientMessage(Component.translatable("entity.graveyard.ghouling.obey"), true);
            }

            return InteractionResultHolder.fail(stack);
        }

        if (owned && startFarDraw(world, user, hand, stack)) {
            return InteractionResultHolder.consume(stack);
        }

        if (world.isClientSide) {
            return owned ? InteractionResultHolder.sidedSuccess(stack, true) : super.use(world, user, hand);
        }

        if (LegacyItemData.isStored(stack)) {
            user.displayClientMessage(Component.translatable("entity.graveyard.ghouling.resting"), true);
            return InteractionResultHolder.sidedSuccess(stack, false);
        }

        UUID ghoulingUUID = LegacyItemData.ghoulingUuid(stack);

        if (ghoulingUUID != null && owned) {
            if (user.isShiftKeyDown()) {
                if (world instanceof ServerLevel serverWorld) {
                    call(serverWorld, user, stack, ghoulingUUID);
                }
            } else {
                GhoulingEntity ghouling = world.getEntitiesOfClass(GhoulingEntity.class, user.getBoundingBox().inflate(100), Entity::isAlive)
                        .stream()
                        .filter(entity -> entity.getUUID().compareTo(ghoulingUUID) == 0)
                        .findFirst()
                        .orElse(null);

                if (ghouling != null && isOwnedBy(ghouling, user) && !rejectsStaff(ghouling, stack, user)) {
                    Predicate<Entity> predicate = entity -> {
                        if (entity instanceof LivingEntity target && ghouling.canAttack(target) && ghouling.canAttackWithOwner(target, user)) {
                            if (entity instanceof TamableAnimal tameableEntity) {
                                if (tameableEntity.getOwnerUUID() != null) {
                                    return tameableEntity.getOwnerUUID().compareTo(ownerId) != 0;
                                }
                            } else if (entity instanceof GraveyardMinionEntity minion) {
                                if (minion.getOwnerUuid() != null) {
                                    return minion.getOwnerUuid().compareTo(ownerId) != 0;
                                }
                            }

                            return true;
                        }

                        return false;
                    };
                    int distance = 100;
                    Vec3 start = user.getEyePosition();
                    Vec3 end = clipAtBlocks(user, start, start.add(user.getViewVector(1.0F).scale(distance)));
                    Vec3 rot = end.subtract(start);
                    AABB box = user.getBoundingBox().expandTowards(rot).inflate(1.0D);
                    HitResult result = ProjectileUtil.getEntityHitResult(user, start, end, box, predicate, distance * distance);
                    ItemEntity targetItem = findItemInSight(user, result);

                    if (targetItem != null) {
                        if (!ghouling.hasCoffin()) {
                            user.displayClientMessage(Component.translatable("entity.graveyard.ghouling.no_coffin"), true);
                        } else if (ghouling.isCoffinFull()) {
                            user.displayClientMessage(Component.translatable("entity.graveyard.ghouling.coffin_full"), true);
                        } else {
                            ghouling.setTarget(null);
                            ghouling.setAggressive(false);
                            ghouling.setSitting(false);
                            ghouling.collect(targetItem);
                            user.displayClientMessage(Component.translatable("entity.graveyard.ghouling.collect"), true);
                        }
                    } else if (result != null && result.getType() == HitResult.Type.ENTITY) {
                        Entity entity = ((EntityHitResult) result).getEntity();

                        if (entity instanceof LivingEntity livingEntity) {
                            user.displayClientMessage(Component.translatable("entity.graveyard.ghouling.kill"), true);
                            ghouling.playAttackSound = true;
                            ghouling.setTarget(livingEntity);
                            ghouling.setAggressive(true);
                            ghouling.setSitting(false);
                        }
                    }
                }
            }
        }

        return owned ? InteractionResultHolder.sidedSuccess(stack, false) : super.use(world, user, hand);
    }

}
