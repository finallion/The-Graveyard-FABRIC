package com.lion.graveyard.item;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.init.TGItems;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.registry.tag.BlockTags;


public class DaggerItem extends SwordItem {

    public DaggerItem(ToolMaterial material, float effectiveDamage, float effectiveSpeed, Item.Settings settings) {
        super(material, (int) (effectiveDamage - material.getAttackDamage()), effectiveSpeed, settings);
    }

    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        if (state.isOf(Blocks.COBWEB) || state.getBlock() instanceof GlassBlock || state.getBlock() instanceof PaneBlock || state.getBlock() instanceof StainedGlassBlock || state.getBlock() instanceof StainedGlassPaneBlock) {
            return 30.0F;
        } else {
            return state.isIn(BlockTags.SWORD_EFFICIENT) ? 1.5F : 1.0F;
        }
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (target.isDead() && Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").isBloodCollectableEntity.contains(target.getType().toString()) && attacker instanceof PlayerEntity playerEntity) {
            ItemStack stackOffhand = attacker.getOffHandStack();
            if (stackOffhand.isOf(TGItems.VIAL_OF_BLOOD.get()) && VialOfBlood.getBlood(stackOffhand) < 0.8F) {
                VialOfBlood.setBlood(stackOffhand, VialOfBlood.getBlood(stackOffhand) + 0.1F);
            } else if (stackOffhand.isOf(Items.GLASS_BOTTLE)) {
                stackOffhand.decrement(1);
                giveVial(playerEntity, stackOffhand.isEmpty() ? 40 : -1);
            } else if (!fillHotbarVial(playerEntity)) {
                convertHotbarBottle(playerEntity);
            }

        }

        return super.postHit(stack, target, attacker);
    }

    private static boolean fillHotbarVial(PlayerEntity player) {
        for (int slot = 0; slot < 9; slot++) {
            ItemStack candidate = player.getInventory().getStack(slot);
            if (candidate.isOf(TGItems.VIAL_OF_BLOOD.get()) && VialOfBlood.getBlood(candidate) < 0.8F) {
                VialOfBlood.setBlood(candidate, VialOfBlood.getBlood(candidate) + 0.1F);
                return true;
            }
        }
        return false;
    }

    private static void convertHotbarBottle(PlayerEntity player) {
        for (int slot = 0; slot < 9; slot++) {
            ItemStack candidate = player.getInventory().getStack(slot);
            if (candidate.isOf(Items.GLASS_BOTTLE)) {
                candidate.decrement(1);
                giveVial(player, candidate.isEmpty() ? slot : -1);
                return;
            }
        }
    }

    private static void giveVial(PlayerEntity player, int preferredSlot) {
        ItemStack vial = new ItemStack(TGItems.VIAL_OF_BLOOD.get());
        if (preferredSlot >= 0) {
            player.getInventory().setStack(preferredSlot, vial);
        } else if (!player.getInventory().insertStack(vial)) {
            player.dropItem(vial, false);
        }
    }
}
