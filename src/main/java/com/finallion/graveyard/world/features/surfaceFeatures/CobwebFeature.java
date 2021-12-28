package com.finallion.graveyard.world.features.surfaceFeatures;

import com.mojang.serialization.Codec;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Random;

public class CobwebFeature extends Feature<DefaultFeatureConfig> {


    public CobwebFeature(Codec<DefaultFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos blockPos = context.getOrigin();
        Random random = context.getRandom();
        context.getConfig();
        BlockPos.Mutable mutable = new BlockPos.Mutable().set(blockPos);

        for (int i = 64; i < world.getHeight(); i++) {
            mutable.set(blockPos);
            mutable.move(random.nextInt(10) - random.nextInt(10), 0, random.nextInt(10) - random.nextInt(10));
            mutable.setY(i);

            if (world.getBlockState(mutable).isOf(Blocks.SPRUCE_LEAVES) && world.getBlockState(mutable.down()).isAir() && random.nextBoolean()) {
                world.setBlockState(mutable.down(), Blocks.COBWEB.getDefaultState(), 2);
                break;
            }
        }

        return true;
    }
}