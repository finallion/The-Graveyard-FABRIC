package com.finallion.graveyard.biomes.features.surfaceFeatures;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.ChunkRegion;
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

    // because the feature placer "moves", it can happen that it tries to setBlock in an unloaded chunk
    // causing Mojang to spam "Detected setBlock in a far chunk" in the logs (method_37368 in ChunkRegion)
    // this method checks if the chunk is a valid spawn
    // Chunkpos: [36, 30] -> [36, 30] valid
    // Chunkpos: [36, 30] -> [36, 32] not valid
    public static boolean isInLoadedChunk(StructureWorldAccess world, BlockPos posBeforeShift, BlockPos posAfterShift) {
        int x1 = world.getChunk(posBeforeShift).getPos().x;
        int z1 = world.getChunk(posBeforeShift).getPos().z;
        int x2 = world.getChunk(posAfterShift).getPos().x;
        int z2 = world.getChunk(posAfterShift).getPos().z;

        return world.getChunk(posBeforeShift) == world.getChunk(posAfterShift);
    }
}
