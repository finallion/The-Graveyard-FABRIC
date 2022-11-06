package com.finallion.graveyard.item;


import com.finallion.graveyard.init.TGAdvancements;
import com.finallion.graveyard.init.TGItems;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class DaggerItem extends SwordItem {

    public DaggerItem(ToolMaterial material, float effectiveDamage, float effectiveSpeed, Item.Settings settings) {
        super(material, (int) (effectiveDamage - material.getAttackDamage()), effectiveSpeed, settings);
    }

    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        if (state.isOf(Blocks.COBWEB) || state.getBlock() instanceof GlassBlock || state.getBlock() instanceof PaneBlock || state.getBlock() instanceof StainedGlassBlock || state.getBlock() instanceof StainedGlassPaneBlock) {
            return 30.0F;
        } else {
            Material material = state.getMaterial();
            return material != Material.PLANT && material != Material.REPLACEABLE_PLANT && !state.isIn(BlockTags.LEAVES) && material != Material.GOURD ? 1.0F : 1.5F;
        }
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (target.isDead() && target instanceof VillagerEntity && attacker instanceof PlayerEntity playerEntity) {
            ItemStack stackOffhand = attacker.getOffHandStack();
            if (stackOffhand.isOf(TGItems.VIAL_OF_BLOOD)) {
                float blood = VialOfBlood.getBlood(stackOffhand);
                VialOfBlood.setBlood(stackOffhand, blood + 0.1F);
            } else if (stackOffhand.isOf(Items.GLASS_BOTTLE)) {
                stackOffhand.decrement(1);
                ItemStack vial = new ItemStack(TGItems.VIAL_OF_BLOOD);
                if (stackOffhand.isEmpty()) {
                    playerEntity.getInventory().insertStack(40, vial);
                } else {
                    playerEntity.getInventory().insertStack(vial);
                }
            }
        }

        return super.postHit(stack, target, attacker);
    }
}
