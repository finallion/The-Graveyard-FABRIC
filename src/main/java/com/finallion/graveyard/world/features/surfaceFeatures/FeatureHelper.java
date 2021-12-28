package com.finallion.graveyard.world.features.surfaceFeatures;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

public class FeatureHelper {

    public static boolean canBePlaced(StructureWorldAccess world, BlockPos pos, BlockState state) {
        if (state.isOf(Blocks.GRASS_BLOCK)) {
            world.setBlockState(pos, Blocks.DIRT.getDefaultState(), 2);
        }

        return state.isOf(Blocks.MOSS_BLOCK) || state.isOf(Blocks.GRASS_BLOCK) || state.isOf(Blocks.COARSE_DIRT) || state.isOf(Blocks.ROOTED_DIRT);
    }

    public static boolean canBePlaced(BlockState state) {
        return state.isOf(Blocks.MOSS_BLOCK) || state.isOf(Blocks.GRASS_BLOCK) || state.isOf(Blocks.COARSE_DIRT) || state.isOf(Blocks.ROOTED_DIRT);
    }

    public static boolean canGenerate(StructureWorldAccess world, BlockPos pos, int height) {
        for (int i = pos.getY(); i <= pos.getY() + height; i++) {
            if (!world.getBlockState(pos).isAir()) {
                return false;
            }
        }
        return true;

    }

}
