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

public class MoosCarpetFeature extends Feature<DefaultFeatureConfig> {


    public MoosCarpetFeature(Codec<DefaultFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos blockPos = context.getOrigin();
        Random random = context.getRandom();
        context.getConfig();
        BlockPos.Mutable mutable = new BlockPos.Mutable().set(blockPos);

        // cap at height 80
        for (int i = 64; i < 80; i++) {
            mutable.move(random.nextInt(4) - random.nextInt(4), 0, random.nextInt(4) - random.nextInt(4));
            mutable.setY(i);
            if (world.getBlockState(mutable).getBlock() instanceof LeavesBlock && world.getBlockState(mutable.up()).isAir()) {
                world.setBlockState(mutable.up(), Blocks.MOSS_CARPET.getDefaultState(), 2);
                break;

            } else if (world.getBlockState(mutable).isOf(Blocks.MOSS_BLOCK) && world.getBlockState(mutable.up()).isAir()) {
                if (random.nextInt(5) == 0) {
                    world.setBlockState(mutable.up(), Blocks.MOSS_CARPET.getDefaultState(), 2);
                    break;
                }
            }
        }

        return true;

    }

}
