package com.lion.graveyard.blocks;

import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;

public class TGStairsBlock extends StairBlock {

    // access to private stairs constructor
    public TGStairsBlock(BlockState baseBlockState, Properties settings) {
        super(baseBlockState, settings);
    }
}
