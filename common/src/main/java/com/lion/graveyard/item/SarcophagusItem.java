package com.lion.graveyard.item;


import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class SarcophagusItem extends BlockItem {
    public SarcophagusItem(Properties settings, Block block) {
        super(block, settings);
    }

    @Override
    protected boolean canPlace(BlockPlaceContext context, BlockState state) {
        return context.getLevel().setBlock(context.getClickedPos(), state, 26);
    }

}
