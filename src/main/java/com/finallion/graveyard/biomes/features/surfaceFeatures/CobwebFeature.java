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

        // cap at height 100
        for (int i = 64; i < 100; i++) {
            mutable.move(random.nextInt(4) - random.nextInt(4), 0, random.nextInt(4) - random.nextInt(4));
            mutable.setY(i);
            if (world.getBlockState(mutable).isOf(Blocks.SPRUCE_LEAVES) && world.getBlockState(mutable.down()).isAir()) {
                System.out.println(mutable.down());
                BlockPos setPos = mutable.down();
                world.setBlockState(setPos, Blocks.COBWEB.getDefaultState(), 2);
                System.out.println(setPos);
                System.out.println("----");
                break;
            }
        }

        return true;
    }
}