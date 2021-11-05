package com.finallion.graveyard.biomes.features;

import com.mojang.serialization.Codec;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Random;

public class MoosCarpetFeature extends Feature<DefaultFeatureConfig> {


    public MoosCarpetFeature(Codec<DefaultFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess structureWorldAccess = context.getWorld();
        BlockPos blockPos = context.getOrigin();
        context.getConfig();

        if (!structureWorldAccess.isAir(blockPos)) {
            return false;
        } else {
            if (structureWorldAccess.getBlockState(blockPos.down()).isOf(Blocks.STRIPPED_SPRUCE_LOG)) {
                structureWorldAccess.setBlockState(blockPos, Blocks.MOSS_CARPET.getDefaultState(), 2);
                return true;
            } else if ((structureWorldAccess.getBlockState(blockPos.down()).isOf(Blocks.SPRUCE_LEAVES) || structureWorldAccess.getBlockState(blockPos.down()).isOf(Blocks.MOSS_BLOCK))) {
                structureWorldAccess.setBlockState(blockPos, Blocks.MOSS_CARPET.getDefaultState(), 2);
                return true;
            }
        }

        return false;

    }

}
