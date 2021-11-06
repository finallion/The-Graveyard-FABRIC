package com.finallion.graveyard.biomes.features.surfaceFeatures;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

public class FeatureHelper {

    public static boolean canBePlaced(BlockState state) {
        return state == Blocks.MOSS_BLOCK.getDefaultState() || state == Blocks.GRASS_BLOCK.getDefaultState() || state == Blocks.COARSE_DIRT.getDefaultState() || state == Blocks.ROOTED_DIRT.getDefaultState();
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
