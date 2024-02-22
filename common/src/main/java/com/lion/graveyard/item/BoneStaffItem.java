package com.lion.graveyard.item;

import com.lion.graveyard.entities.GhoulingEntity;
import com.lion.graveyard.entities.GraveyardMinionEntity;
import com.lion.graveyard.init.TGEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.function.Predicate;

public class BoneStaffItem extends Item {
    private final byte ghoulVariant;
    public static Map<UUID, UUID> ownerGhoulingMapping = new HashMap<>();

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

        if (player != null && !world.isClientSide()) {
            // TAG OWNER UUID CHECK
            /* Does the OwnerUUID in the NBT match the user of the staff*/
            if (stack.getTag() != null && stack.getTag().contains("OwnerUUID")) {
                if (stack.getTag().getUUID("OwnerUUID").compareTo(player.getUUID()) != 0) {
                    return InteractionResult.PASS;
                }
            }

            /* Is the Ghouling with the UUID saved in the NBT still alive?*/
            if (stack.getTag() != null && stack.getTag().contains("GhoulingUUID")) {
                if (ownerGhoulingMapping.containsKey(stack.getTag().getUUID("GhoulingUUID"))) {
                    return InteractionResult.PASS;
                }
            }

            /*
            If owner match and Ghouling dead, summon new one
            - Create new NBT
            - Pass data to Ghouling
            - Save Owner-Ghouling in Map
             */
            GhoulingEntity ghouling = TGEntities.GHOULING.get().create(world);
            ghouling.moveTo(blockPos.above(), 0.0F, 0.0F);
            ghouling.setOwner(player);
            ghouling.setVariant(ghoulVariant);

            /* TAG INPUTS BOUND TO ITEM STACK */
            stack.getOrCreateTag().putUUID("GhoulingUUID", ghouling.getUUID());

            if (!stack.getTag().contains("OwnerUUID")) {
                stack.getOrCreateTag().putUUID("OwnerUUID", player.getUUID());
                player.displayClientMessage(Component.translatable("entity.graveyard.ghouling.spawn"), true);
            } else {
                player.displayClientMessage(Component.translatable("entity.graveyard.ghouling.respawn"), true);
            }

            /* END TAG INPUT */
            ownerGhoulingMapping.putIfAbsent(ghouling.getUUID(), player.getUUID());

            ghouling.setStaff(stack); // pass stack to ghouling
            ghouling.onSummoned();
            world.addFreshEntity(ghouling);
            return InteractionResult.SUCCESS;
        }


        //
        //if (!world.isClient && player != null && tag != null && tag.contains("GhoulingUUID") && tag.contains("OwnerUUID")) {
        //    if (tag.getUUID("OwnerUUID").compareTo(player.getUUID()) != 0) {
        //        return InteractionResult.PASS;
        //    }
        //
        //    UUID ghoulingUUID = tag.getUUID("GhoulingUUID");
        //    GhoulingEntity ghouling = world.getEntitiesByClass(GhoulingEntity.class, player.getBoundingBox().expand(100), Objects::nonNull).stream().filter(entity -> entity.getUUID().compareTo(ghoulingUUID) == 0).findFirst().orElseThrow();
        //
        //    ghouling.removeCollectGoal();
        //    ghouling.setTarget(null);
        //    ghouling.setAttacking(false);
        //    ghouling.setCanCollect(true);
        //    System.out.println("Set Goal to " + world.getBlockState(blockPos).getBlock() + " at " + blockPos);
        //    ghouling.setCollectGoal(new BreakBlockGoal(ghouling, world.getBlockState(blockPos).getBlock(), blockPos));
        //}


        return super.useOn(context);
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack stack = user.getMainHandItem();
        CompoundTag nbt = stack.getTag();
        if (!world.isClientSide) {
            if (nbt != null && nbt.contains("GhoulingUUID") && nbt.contains("OwnerUUID")) {
                if (user.getUUID().compareTo(nbt.getUUID("OwnerUUID")) != 0) { // case wrong owner
                    user.displayClientMessage(Component.translatable("entity.graveyard.ghouling.obey"), true);
                    return InteractionResultHolder.fail(stack);
                } else {
                    UUID ghoulingUUID = nbt.getUUID("GhoulingUUID");
                    GhoulingEntity ghouling = world.getEntitiesOfClass(GhoulingEntity.class, user.getBoundingBox().inflate(100), Objects::nonNull).stream().filter(entity -> entity.getUUID().compareTo(ghoulingUUID) == 0).findFirst().orElse(null);
                    if (ghouling != null) {
                        if (user.isCrouching()) {
                            ghouling.setTarget(null);
                            ghouling.setAggressive(false);
                            ghouling.setTeleportTimer(15);
                            //ghouling.removeCollectGoal();
                            //ghouling.setCanCollect(false);
                            ghouling.teleportTo(user.getX(), user.getY(), user.getZ());
                        } else {
                            Predicate<Entity> predicate = entity -> {
                                if (entity instanceof LivingEntity) {
                                    if (entity instanceof TamableAnimal tameableEntity) {
                                        if (tameableEntity.getOwnerUUID() != null) {
                                            return tameableEntity.getOwnerUUID().compareTo(nbt.getUUID("OwnerUUID")) != 0;
                                        }
                                    } else if (entity instanceof GraveyardMinionEntity minion) {
                                        if (minion.getOwnerUuid() != null) {
                                            return minion.getOwnerUuid().compareTo(nbt.getUUID("OwnerUUID")) != 0;
                                        }
                                    }
                                    return true;
                                }
                                return false;
                            };
                            int distance = 100;
                            Vec3 start = user.getEyePosition();
                            Vec3 rot = user.getViewVector(1.0F).scale(distance);
                            Vec3 end = start.add(rot);
                            AABB box = user.getBoundingBox().expandTowards(rot).inflate(1.0D);
                            HitResult result = ProjectileUtil.getEntityHitResult(user, start, end, box, predicate, distance * distance);
                            if (result != null && result.getType() == HitResult.Type.ENTITY) {
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
            }
        }

        return super.use(world, user, hand);
    }

}
