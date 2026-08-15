package com.lion.graveyard.item;

import com.lion.graveyard.blocks.CarvedGravestoneBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

/// Names itself after the block it was carved from, so a stack of gravestones is telling apart in
/// the inventory even though they all share one item.
public class CarvedGravestoneItem extends BlockItem {

    public CarvedGravestoneItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public Text getName(ItemStack stack) {
        Block material = CarvedGravestoneBlock.readMaterial(stack);
        return material == null
                ? super.getName(stack)
                : Text.translatable(this.getTranslationKey() + ".of", material.getName());
    }
}
