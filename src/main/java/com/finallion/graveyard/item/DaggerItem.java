package com.finallion.graveyard.item;


import net.minecraft.block.*;
import net.minecraft.item.*;
import net.minecraft.tag.BlockTags;

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

}
