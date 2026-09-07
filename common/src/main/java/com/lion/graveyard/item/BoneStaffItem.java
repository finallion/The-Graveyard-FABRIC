package com.lion.graveyard.item;

import com.lion.graveyard.entities.GhoulingBindings;
import com.lion.graveyard.entities.GhoulingEntity;
import com.lion.graveyard.entities.GraveyardMinionEntity;
import com.lion.graveyard.init.TGEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;

/// Summons one Ghouling per staff and keeps it within reach of its owner.
///
/// The bond is the pair `OwnerUUID` / `GhoulingUUID` in the stack NBT plus the record in
/// [GhoulingBindings]; the Ghouling itself may be unloaded, dead, or in another dimension, and
/// the staff has to tell those apart without ever holding an entity reference.
public class BoneStaffItem extends Item {
    /// Owner distance beyond which a standing Ghouling is pulled back to the owner.
    private static final double FETCH_DISTANCE_SQ = 24 * 24;

    /// Loads the chunk a stranded Ghouling was last seen in, just long enough to teleport it out.
    /// The ticket expires on its own, so nothing has to remember to release it.
    private static final ChunkTicketType<UUID> FETCH_TICKET =
            ChunkTicketType.create("graveyard_ghouling_fetch", UUID::compareTo, 60);

    /// World time at which a running call gives up; optional in the stack NBT.
    private static final String CALL_KEY = "FetchCall";

    /// How long a call keeps waiting for a chunk that is still on its way.
    private static final int CALL_TIMEOUT = 200;

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

        if (player != null && world instanceof ServerWorld serverWorld) {
            NbtCompound nbt = stack.getNbt();
            if (nbt != null && nbt.containsUuid("OwnerUUID") && !nbt.getUuid("OwnerUUID").equals(player.getUuid())) {
                return ActionResult.PASS;
            }
            if (nbt != null && nbt.containsUuid("GhoulingUUID") && isAlive(serverWorld, nbt.getUuid("GhoulingUUID"))) {
                return ActionResult.PASS;
            }

            GhoulingEntity ghouling = TGEntities.GHOULING.get().create(world);
            ghouling.refreshPositionAndAngles(blockPos.up(), 0.0F, 0.0F);
            ghouling.setOwner(player);
            ghouling.setVariant(ghoulVariant);

            stack.getOrCreateNbt().putUuid("GhoulingUUID", ghouling.getUuid());
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
            return ActionResult.SUCCESS;
        }

        return super.useOnBlock(context);
    }

    /// A Ghouling counts as alive while it is loaded somewhere or still has a binding record —
    /// the record is only dropped on death, never on unloading.
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
        if (nbt == null || !nbt.containsUuid("GhoulingUUID") || !nbt.containsUuid("OwnerUUID")
                || !nbt.getUuid("OwnerUUID").equals(player.getUuid())) {
            return;
        }

        boolean calling = nbt.getLong(CALL_KEY) > 0L;
        if (calling && world.getTime() > nbt.getLong(CALL_KEY)) {
            nbt.remove(CALL_KEY);
            player.sendMessage(Text.translatable("entity.graveyard.ghouling.lost"), true);
            calling = false;
        }
        // A running call looks every tick so the Ghouling leaves the moment his chunk is in; the
        // automatic leash has no such hurry and stays spread across the players.
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
            // Not loaded: ask for the chunk now, the next pass finds the Ghouling in it.
            if (binding.pos() != null) {
                home.getChunkManager().addTicket(FETCH_TICKET, new ChunkPos(binding.pos()), 2, ghoulingId);
            }
            return;
        }

        if (calling) {
            nbt.remove(CALL_KEY);
            fetch(ghouling, serverWorld, player);
            return;
        }

        boolean farAway = home != serverWorld || ghouling.squaredDistanceTo(player) > FETCH_DISTANCE_SQ;
        if (farAway && !ghouling.isSitting() && !ghouling.isLeashed() && !ghouling.hasVehicle()) {
            fetch(ghouling, serverWorld, player);
        }
    }

    /// The call the player makes himself. Unlike the automatic leash it reaches a sitting Ghouling
    /// and one in another dimension, and it waits for a chunk instead of doing nothing at all.
    private static void call(ServerWorld world, PlayerEntity owner, ItemStack stack, UUID ghoulingId) {
        GhoulingBindings bindings = GhoulingBindings.get(world);
        GhoulingBindings.Binding binding = bindings.get(ghoulingId);
        if (binding == null) {
            return;
        }

        ServerWorld home = world.getServer().getWorld(binding.dimension());
        if (home != null && home.getEntity(ghoulingId) instanceof GhoulingEntity ghouling) {
            stack.removeSubNbt(CALL_KEY);
            fetch(ghouling, world, owner);
            return;
        }

        // Without a last known position there is no chunk to ask for, and bonds imported from the
        // old text file have none. Cutting the bond is the only way back to a Ghouling at all.
        if (home == null || binding.pos() == null) {
            stack.removeSubNbt(CALL_KEY);
            bindings.remove(ghoulingId);
            owner.sendMessage(Text.translatable("entity.graveyard.ghouling.severed"), true);
            return;
        }

        home.getChunkManager().addTicket(FETCH_TICKET, new ChunkPos(binding.pos()), 2, ghoulingId);
        stack.getOrCreateNbt().putLong(CALL_KEY, world.getTime() + CALL_TIMEOUT);
        owner.sendMessage(Text.translatable("entity.graveyard.ghouling.call"), true);
    }

    private static void fetch(GhoulingEntity ghouling, ServerWorld world, PlayerEntity player) {
        ghouling.setTarget(null);
        ghouling.setAttacking(false);
        // A called Ghouling that kept the pose would sit down again right at the feet of his owner.
        ghouling.setSitting(false);
        ghouling.getNavigation().stop();
        ghouling.teleport(world, player.getX(), player.getY(), player.getZ(),
                EnumSet.noneOf(PositionFlag.class), ghouling.getYaw(), ghouling.getPitch());
        // Crossing a dimension replaces the entity instance, so both the effect and the record have
        // to go to whichever one is now live rather than to the copy that was left behind.
        GhoulingEntity arrived = world.getEntity(ghouling.getUuid()) instanceof GhoulingEntity found ? found : ghouling;
        arrived.setTeleportTimer(15);
        GhoulingBindings.get(world).update(arrived);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getMainHandStack();
        NbtCompound nbt = stack.getNbt();
        if (!world.isClient) {
            if (nbt != null && nbt.contains("GhoulingUUID") && nbt.contains("OwnerUUID")) {
                if (user.getUuid().compareTo(nbt.getUuid("OwnerUUID")) != 0) { // case wrong owner
                    user.sendMessage(Text.translatable("entity.graveyard.ghouling.obey"), true);
                    return TypedActionResult.fail(stack);
                } else {
                    UUID ghoulingUUID = nbt.getUuid("GhoulingUUID");
                    if (user.isSneaking()) {
                        if (world instanceof ServerWorld serverWorld) {
                            call(serverWorld, user, stack, ghoulingUUID);
                        }
                    } else {
                        GhoulingEntity ghouling = world.getEntitiesByClass(GhoulingEntity.class, user.getBoundingBox().expand(100), Objects::nonNull).stream().filter(entity -> entity.getUuid().compareTo(ghoulingUUID) == 0).findFirst().orElse(null);
                        if (ghouling != null) {
                            Predicate<Entity> predicate = entity -> {
                                if (entity instanceof LivingEntity) {
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
                            Vec3d rot = user.getRotationVec(1.0F).multiply(distance);
                            Vec3d end = start.add(rot);
                            Box box = user.getBoundingBox().stretch(rot).expand(1.0D);
                            HitResult result = ProjectileUtil.raycast(user, start, end, box, predicate, distance * distance);
                            if (result != null && result.getType() == HitResult.Type.ENTITY) {
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
            }
        }

        return super.use(world, user, hand);
    }

}
