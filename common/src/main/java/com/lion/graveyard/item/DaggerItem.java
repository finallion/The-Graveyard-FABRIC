package com.lion.graveyard.item;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.init.TGItems;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.StainedGlassBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.block.state.BlockState;

public class DaggerItem extends SwordItem {

    public DaggerItem(Tier material, float effectiveDamage, float effectiveSpeed, Item.Properties settings) {
        super(material, (int) (effectiveDamage - material.getAttackDamageBonus()), effectiveSpeed, settings);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (state.is(Blocks.COBWEB) || state.getBlock() instanceof GlassBlock || state.getBlock() instanceof IronBarsBlock || state.getBlock() instanceof StainedGlassBlock || state.getBlock() instanceof StainedGlassPaneBlock) {
            return 30.0F;
        } else {
            return state.is(BlockTags.SWORD_EFFICIENT) ? 1.5F : 1.0F;
        }
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (target.isDeadOrDying() && Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").isBloodCollectableEntity.contains(target.getType().toString()) && attacker instanceof Player playerEntity) {
            ItemStack stackOffhand = attacker.getOffhandItem();

            if (stackOffhand.is(TGItems.VIAL_OF_BLOOD.get()) && VialOfBlood.getBlood(stackOffhand) < 0.8F) {
                VialOfBlood.setBlood(stackOffhand, VialOfBlood.getBlood(stackOffhand) + 0.1F);
            } else if (stackOffhand.is(Items.GLASS_BOTTLE)) {
                stackOffhand.shrink(1);
                giveVial(playerEntity, stackOffhand.isEmpty() ? 40 : -1);
            } else if (!fillHotbarVial(playerEntity)) {
                convertHotbarBottle(playerEntity);
            }

        }

        return super.hurtEnemy(stack, target, attacker);
    }

    private static boolean fillHotbarVial(Player player) {
        for (int slot = 0; slot < 9; slot++) {
            ItemStack candidate = player.getInventory().getItem(slot);

            if (candidate.is(TGItems.VIAL_OF_BLOOD.get()) && VialOfBlood.getBlood(candidate) < 0.8F) {
                VialOfBlood.setBlood(candidate, VialOfBlood.getBlood(candidate) + 0.1F);
                return true;
            }
        }
        return false;
    }

    private static void convertHotbarBottle(Player player) {
        for (int slot = 0; slot < 9; slot++) {
            ItemStack candidate = player.getInventory().getItem(slot);

            if (candidate.is(Items.GLASS_BOTTLE)) {
                candidate.shrink(1);
                giveVial(player, candidate.isEmpty() ? slot : -1);
                return;
            }
        }
    }

    private static void giveVial(Player player, int preferredSlot) {
        ItemStack vial = new ItemStack(TGItems.VIAL_OF_BLOOD.get());

        if (preferredSlot >= 0) {
            player.getInventory().setItem(preferredSlot, vial);
        } else if (!player.getInventory().add(vial)) {
            player.drop(vial, false);
        }
    }
}
