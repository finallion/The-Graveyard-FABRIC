package com.finallion.graveyard.blocks;

import net.minecraft.block.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class TGMossBlock extends MossBlock {


    public TGMossBlock(Settings settings) {
        super(settings);
    }


    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(Blocks.MOSS_BLOCK);
    }

}
