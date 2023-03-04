package com.finallion.graveyard.item;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.entities.GhoulingEntity;
import com.finallion.graveyard.entities.GraveyardMinionEntity;
import com.finallion.graveyard.init.TGEntities;
import com.finallion.graveyard.init.TGParticles;
import com.finallion.graveyard.util.MathUtil;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.Predicate;

public class BoneStaffItem extends Item {
    private final byte ghoulVariant;
    public static Map<UUID, UUID> ownerGhoulingMapping = new HashMap<>();

    public BoneStaffItem(byte ghoulVariant) {
        super(new FabricItemSettings().maxCount(1));
        this.ghoulVariant = ghoulVariant;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();
        ItemStack stack = context.getStack();

        /*
        There only exists one BoneStaffItem, so to use nbt and create a unique mob, that only can be summoned if the
        previous mob died, we need to use ItemStacks nbt.
        On creation, the staff passes an ItemStack to the summoned Ghouling with Player UUID and Ghouling UUID.
        It saves this pair in a map. Why? Because if the Ghouling unloads and reloads, the ItemStack passed and saved in the Ghouling
        will not be the same but a copy. Therefore, there is no way to tell the staff from the Ghouling the difference between being unloaded and dead.
        Or that the Ghoulings status has changed in some other way.
        We only want to summon a new Ghouling if the previous died, not if the previous was unloaded.
        (Searching the server with the Ghouling UUID will return null, if the Ghouling is unloaded, as unloaded entities are saved in chunk nbt.)
        On use, the staff checks with its nbt, if the player using it is the owner of the staff and if there exists a Ghouling with an
        UUID (in the Map) that matches the UUID in the staff ItemStack nbt.
        On death, the Ghouling deletes its entry in the map.
        Other way: a NBT to the player with its Ghoulings UUID ?
         */

        if (player != null && !world.isClient()) {
            // TAG OWNER UUID CHECK
            /* Does the OwnerUUID in the NBT match the user of the staff*/
            if (stack.getNbt() != null && stack.getNbt().contains("OwnerUUID")) {
                if (stack.getNbt().getUuid("OwnerUUID").compareTo(player.getUuid()) != 0) {
                    return ActionResult.PASS;
                }
            }

            /* Is the Ghouling with the UUID saved in the NBT still alive?*/
            if (stack.getNbt() != null && stack.getNbt().contains("GhoulingUUID")) {
                if (ownerGhoulingMapping.containsKey(stack.getNbt().getUuid("GhoulingUUID"))) {
                    return ActionResult.PASS;
                }
            }

            /*
            If owner match and Ghouling dead, summon new one
            - Create new NBT
            - Pass data to Ghouling
            - Save Owner-Ghouling in Map
             */
            GhoulingEntity ghouling = TGEntities.GHOULING.create(world);
            ghouling.refreshPositionAndAngles(blockPos.up(), 0.0F, 0.0F);
            ghouling.setOwner(player);
            ghouling.setVariant(ghoulVariant);

            /* TAG INPUTS BOUND TO ITEM STACK */
            stack.getOrCreateNbt().putUuid("GhoulingUUID", ghouling.getUuid());

            if (!stack.getNbt().contains("OwnerUUID")) {
                stack.getOrCreateNbt().putUuid("OwnerUUID", player.getUuid());
                player.sendMessage(Text.translatable("entity.graveyard.ghouling.spawn"), true);
            } else {
                player.sendMessage(Text.translatable("entity.graveyard.ghouling.respawn"), true);
            }

            /* END TAG INPUT */
            ownerGhoulingMapping.putIfAbsent(ghouling.getUuid(), player.getUuid());

            ghouling.setStaff(stack); // pass stack to ghouling
            ghouling.onSummoned();
            world.spawnEntity(ghouling);
            return ActionResult.SUCCESS;
        }


        //
        //if (!world.isClient && player != null && tag != null && tag.contains("GhoulingUUID") && tag.contains("OwnerUUID")) {
        //    if (tag.getUuid("OwnerUUID").compareTo(player.getUuid()) != 0) {
        //        return ActionResult.PASS;
        //    }
        //
        //    UUID ghoulingUUID = tag.getUuid("GhoulingUUID");
        //    GhoulingEntity ghouling = world.getEntitiesByClass(GhoulingEntity.class, player.getBoundingBox().expand(100), Objects::nonNull).stream().filter(entity -> entity.getUuid().compareTo(ghoulingUUID) == 0).findFirst().orElseThrow();
        //
        //    ghouling.removeCollectGoal();
        //    ghouling.setTarget(null);
        //    ghouling.setAttacking(false);
        //    ghouling.setCanCollect(true);
        //    System.out.println("Set Goal to " + world.getBlockState(blockPos).getBlock() + " at " + blockPos);
        //    ghouling.setCollectGoal(new BreakBlockGoal(ghouling, world.getBlockState(blockPos).getBlock(), blockPos));
        //}


        return super.useOnBlock(context);
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
                    GhoulingEntity ghouling = world.getEntitiesByClass(GhoulingEntity.class, user.getBoundingBox().expand(100), Objects::nonNull).stream().filter(entity -> entity.getUuid().compareTo(ghoulingUUID) == 0).findFirst().orElse(null);
                    if (ghouling != null) {
                        if (user.isSneaking()) {
                            ghouling.setTarget(null);
                            ghouling.setAttacking(false);
                            ghouling.setTeleportTimer(15);
                            //ghouling.removeCollectGoal();
                            //ghouling.setCanCollect(false);
                            ghouling.teleport(user.getX(), user.getY(), user.getZ());
                        } else {
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
