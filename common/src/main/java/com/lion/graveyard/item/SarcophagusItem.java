package com.lion.graveyard.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;

public class SarcophagusItem extends BlockItem {
    public SarcophagusItem(Settings settings, Block block) {
        super(block, settings);
    }

    protected boolean place(ItemPlacementContext context, BlockState state) {
        return context.getWorld().setBlockState(context.getBlockPos(), state, 26);
    }


}
