package com.lion.graveyard.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.state.BlockState;

public class TGGrassBlock extends GrassBlock {

    public TGGrassBlock(Properties settings) {
        super(settings);
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
        return new ItemStack(Blocks.GRASS_BLOCK);
    }


}
