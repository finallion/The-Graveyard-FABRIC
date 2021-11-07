package com.finallion.graveyard.biomes.features.surfaceFeatures;

import com.mojang.serialization.Codec;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Random;

public class MossyBoulderFeature extends Feature<DefaultFeatureConfig> {


    public MossyBoulderFeature(Codec<DefaultFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos blockPos = context.getOrigin();
        Random random = context.getRandom();
        context.getConfig();
        BlockPos.Mutable mutable = new BlockPos.Mutable().set(blockPos);

        if (!FeatureHelper.canBePlaced(world.getBlockState(blockPos.down()))) {
            return false;
        }

        if (!FeatureHelper.canGenerate(world, blockPos, 5)) {
            return false;
        }


        world.setBlockState(mutable, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 2);
        world.setBlockState(mutable.add(0, 0, 1), Blocks.MOSSY_COBBLESTONE.getDefaultState(), 2);
        world.setBlockState(mutable.add(1, 0, 0), Blocks.MOSS_BLOCK.getDefaultState(), 2);
        world.setBlockState(mutable.add(-1, 0, 0), Blocks.COBBLESTONE.getDefaultState(), 2);
        world.setBlockState(mutable.add(0, 0, -1), Blocks.MOSSY_COBBLESTONE.getDefaultState(), 2);
        world.setBlockState(mutable.add(1, 0, 1), Blocks.MOSSY_COBBLESTONE_SLAB.getDefaultState(), 2);
        world.setBlockState(mutable.add(-1, 0, -1), Blocks.MOSSY_COBBLESTONE_SLAB.getDefaultState(), 2);
        world.setBlockState(mutable.add(-1, 0, 1), Blocks.MOSS_BLOCK.getDefaultState(), 2);
        world.setBlockState(mutable.add(1, 0, -1), Blocks.MOSSY_COBBLESTONE_SLAB.getDefaultState(), 2);
        world.setBlockState(mutable.add(2, 0, 0), Blocks.MOSS_BLOCK.getDefaultState(), 2);
        world.setBlockState(mutable.add(-2, 0, 0), Blocks.COBBLESTONE_SLAB.getDefaultState(), 2);

        world.setBlockState(mutable.add(0, 1, 0), Blocks.MOSSY_COBBLESTONE.getDefaultState(), 2);
        world.setBlockState(mutable.add(0, 1, 1), Blocks.COBBLESTONE_SLAB.getDefaultState(), 2);
        world.setBlockState(mutable.add(1, 1, 0), Blocks.MOSSY_COBBLESTONE_SLAB.getDefaultState(), 2);
        world.setBlockState(mutable.add(-1, 1, 0), Blocks.MOSSY_COBBLESTONE_SLAB.getDefaultState(), 2);
        world.setBlockState(mutable.add(0, 1, -1), Blocks.COBBLESTONE_SLAB.getDefaultState(), 2);



        return true;
    }
}
