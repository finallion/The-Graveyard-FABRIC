package com.finallion.graveyard.biomes.features.trees;

import com.finallion.graveyard.biomes.features.TGTreeFeatureConfig;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Random;

public class SmallSpruceTree01 extends BaseSpruceTree {


    public SmallSpruceTree01(Codec<TGTreeFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<TGTreeFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos blockPos = context.getOrigin();
        BlockPos.Mutable mutable = new BlockPos.Mutable().set(blockPos);
        BlockState wood = context.getConfig().woodState;
        BlockState leaf = context.getConfig().leafState;

        if (world.getBlockState(blockPos.down()) != Blocks.MOSS_BLOCK.getDefaultState()) return false;

        for (int i = 0; i < 12; i++) {
            world.setBlockState(blockPos.up(i), wood, 2);
            mutable.move(0, 1, 0);
        }

        // single tree top
        world.setBlockState(mutable.add(0, 3, 0), leaf, 2);
        world.setBlockState(mutable.add(0, 2, 0), leaf, 2);
        world.setBlockState(mutable.add(0, 1, 0), leaf, 2);
        world.setBlockState(mutable.add(0, 0, 0), leaf, 2);

        generateOneStar(context, mutable.add(0, 0, 0), false);
        generateOneStar(context, mutable.add(0, -1, 0), false);

        // second layer
        generateOneStar(context, mutable.add(0, -3, 0), false);
        generateTwoStar(context, mutable.add(0, -4, 0), false);

        world.setBlockState(mutable.add(1, -5, 0), leaf, 2);
        world.setBlockState(mutable.add(0, -5, 1), leaf, 2);
        world.setBlockState(mutable.add(-1, -5, -1), leaf, 2);

        // third layer
        generateTwoStar(context, mutable.add(0, -6, 0), false);
        generateThreeStar(context, mutable.add(0, -7, 0), true);

        world.setBlockState(mutable.add(1, -8, 0), leaf, 2);
        world.setBlockState(mutable.add(0, -8, 1), leaf, 2);
        world.setBlockState(mutable.add(0, -8, -1), leaf, 2);

        // forth layer
        generateTwoStar(context, mutable.add(0, -9, 0), false);
        generateThreeStar(context, mutable.add(0, -10, 0), false);


        return false;
    }
}
