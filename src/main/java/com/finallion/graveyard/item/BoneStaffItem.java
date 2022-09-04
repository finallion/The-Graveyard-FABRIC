package com.finallion.graveyard.item;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.entities.GhoulingEntity;
import com.finallion.graveyard.entities.GraveyardMinionEntity;
import com.finallion.graveyard.init.TGEntities;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
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
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.sql.SQLOutput;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;

public class BoneStaffItem extends Item {
    private final byte ghoulVariant;

    public BoneStaffItem(byte ghoulVariant) {
        super(new FabricItemSettings().maxCount(1).group(TheGraveyard.GROUP));
        this.ghoulVariant = ghoulVariant;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();
        ItemStack stack = context.getStack();
        NbtCompound tag = stack.getNbt();

        if (!world.isClient && player != null && (tag == null || !tag.getBoolean("isAlive"))) {
            // owner check
            if (tag != null && tag.contains("OwnerUUID")) {
                if (tag.getUuid("OwnerUUID").compareTo(player.getUuid()) != 0) {
                    return ActionResult.PASS;
                }
            }

            GhoulingEntity ghouling = (GhoulingEntity) TGEntities.GHOULING.create(world);
            ghouling.refreshPositionAndAngles(blockPos.up(), 0.0F, 0.0F);
            ghouling.setOwner(player);
            ghouling.onSummoned(stack);
            ghouling.setVariant(ghoulVariant);
            world.spawnEntity(ghouling);

            stack.getOrCreateNbt().putUuid("GhoulingUUID", ghouling.getUuid());
            stack.getOrCreateNbt().putBoolean("isAlive", true);

            // owner is set on first summon
            if (tag == null || !tag.contains("OwnerUUID")) {
                stack.getOrCreateNbt().putUuid("OwnerUUID", player.getUuid());
                player.sendMessage(Text.literal("I hear and obey."));
            } else {
                player.sendMessage(Text.literal("Death is a mere inconvenience."));
            }

            return ActionResult.SUCCESS;
        }
        /*

        if (!world.isClient && player != null && tag != null && tag.contains("GhoulingUUID") && tag.contains("OwnerUUID")) {
            if (tag.getUuid("OwnerUUID").compareTo(player.getUuid()) != 0) {
                return ActionResult.PASS;
            }

            UUID ghoulingUUID = tag.getUuid("GhoulingUUID");
            GhoulingEntity ghouling = world.getEntitiesByClass(GhoulingEntity.class, player.getBoundingBox().expand(100), Objects::nonNull).stream().filter(entity -> entity.getUuid().compareTo(ghoulingUUID) == 0).findFirst().orElseThrow();

            ghouling.removeCollectGoal();
            ghouling.setTarget(null);
            ghouling.setAttacking(false);
            ghouling.setCanCollect(true);
            System.out.println("Set Goal to " + world.getBlockState(blockPos).getBlock() + " at " + blockPos);
            ghouling.setCollectGoal(new BreakBlockGoal(ghouling, world.getBlockState(blockPos).getBlock(), blockPos));
        }

         */

        return super.useOnBlock(context);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getMainHandStack();
        NbtCompound tag = stack.getNbt();
        if (!world.isClient) {
            //System.out.println("Tag: " + tag);
            if (tag != null && tag.contains("GhoulingUUID") && tag.contains("OwnerUUID") && tag.contains("isAlive")) {
                if (user.getUuid().compareTo(tag.getUuid("OwnerUUID")) != 0) { // case wrong owner
                    user.sendMessage(Text.literal("I don't obey your orders, you are no master of mine!"));
                    return TypedActionResult.fail(stack);
                } else if (!tag.getBoolean("isAlive")) { // case no alive ghouling
                    return TypedActionResult.fail(stack);
                } else {
                    UUID ghoulingUUID = tag.getUuid("GhoulingUUID");
                    GhoulingEntity ghouling = world.getEntitiesByClass(GhoulingEntity.class, user.getBoundingBox().expand(100), Objects::nonNull).stream().filter(entity -> entity.getUuid().compareTo(ghoulingUUID) == 0).findFirst().orElse(null);
                    if (ghouling != null) {
                        if (user.isSneaking()) {
                            ghouling.setTarget(null);
                            ghouling.setAttacking(false);
                            //ghouling.removeCollectGoal();
                            //ghouling.setCanCollect(false);
                            ghouling.teleport(user.getX(), user.getY(), user.getZ());
                        } else {
                            Predicate<Entity> predicate = entity -> {
                                if (entity instanceof LivingEntity) {
                                    if (entity instanceof TameableEntity tameableEntity) {
                                        if (tameableEntity.getOwnerUuid() != null) {
                                            return tameableEntity.getOwnerUuid().compareTo(tag.getUuid("OwnerUUID")) != 0;
                                        }
                                    } else if (entity instanceof GraveyardMinionEntity minion) {
                                        if (minion.getOwnerUuid() != null) {
                                            return minion.getOwnerUuid().compareTo(tag.getUuid("OwnerUUID")) != 0;
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
