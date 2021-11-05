package com.finallion.graveyard.biomes.features.trees;

import com.finallion.graveyard.biomes.features.TGTreeFeatureConfig;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Random;
import java.util.function.BiConsumer;

public abstract class BaseSpruceTree extends Feature<TGTreeFeatureConfig> {


    public BaseSpruceTree(Codec<TGTreeFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<TGTreeFeatureConfig> context) {
        return false;
    }

    public void generateOneStar(FeatureContext<TGTreeFeatureConfig> context, BlockPos pos, boolean beSquare) {
        /*
                #
              # o #
                #
         */
        context.getWorld().setBlockState(pos.add(1, 0, 0), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(0, 0, 1), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(-1, 0, 0), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(0, 0, -1), context.getConfig().leafState, 2);

    }


    public void generateTwoStar(FeatureContext<TGTreeFeatureConfig> context, BlockPos pos, boolean beSquare) {
        /*
                #
              # # #
            # # o # #
              # # #
                #
         */

        context.getWorld().setBlockState(pos.add(1, 0, 0), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(0, 0, 1), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(-1, 0, 0), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(0, 0, -1), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(-1, 0, -1), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(1, 0, 1), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(-1, 0, 1), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(1, 0, -1), context.getConfig().leafState, 2);

        if (!beSquare) {
            context.getWorld().setBlockState(pos.add(2, 0, 0), context.getConfig().leafState, 2);
            context.getWorld().setBlockState(pos.add(0, 0, 2), context.getConfig().leafState, 2);
            context.getWorld().setBlockState(pos.add(-2, 0, 0), context.getConfig().leafState, 2);
            context.getWorld().setBlockState(pos.add(0, 0, -2), context.getConfig().leafState, 2);
        }
    }



    public void generateThreeStar(FeatureContext<TGTreeFeatureConfig> context, BlockPos pos, boolean beSquare) {
        /*
                #
              # # #
            # # # # #
          # # # o # # #
            # # # # #
              # # #
                #
         */

        context.getWorld().setBlockState(pos.add(1, 0, 0), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(0, 0, 1), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(-1, 0, 0), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(0, 0, -1), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(-1, 0, -1), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(1, 0, 1), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(-1, 0, 1), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(1, 0, -1), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(2, 0, 0), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(0, 0, 2), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(-2, 0, 0), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(0, 0, -2), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(2, 0, 1), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(1, 0, 2), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(-2, 0, 1), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(1, 0, -2), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(2, 0, -1), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(-1, 0, 2), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(-2, 0, -1), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(-1, 0, -2), context.getConfig().leafState, 2);

        if (!beSquare) {
            context.getWorld().setBlockState(pos.add(3, 0, 0), context.getConfig().leafState, 2);
            context.getWorld().setBlockState(pos.add(0, 0, 3), context.getConfig().leafState, 2);
            context.getWorld().setBlockState(pos.add(-3, 0, 0), context.getConfig().leafState, 2);
            context.getWorld().setBlockState(pos.add(0, 0, -3), context.getConfig().leafState, 2);
        }

    }


    public void generateFourStar(FeatureContext<TGTreeFeatureConfig> context, BlockPos pos, boolean beSquare) {
        /*
                  #
                # # #
              # # # # #
            # # # # # # #
          # # # # o # # # #
            # # # # # # #
              # # # # #
                # # #
                  #
         */

        context.getWorld().setBlockState(pos.add(1, 0, 0), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(0, 0, 1), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(-1, 0, 0), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(0, 0, -1), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(-1, 0, -1), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(1, 0, 1), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(-1, 0, 1), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(1, 0, -1), context.getConfig().leafState, 2);

        context.getWorld().setBlockState(pos.add(2, 0, 0), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(0, 0, 2), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(-2, 0, 0), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(0, 0, -2), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(2, 0, 1), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(1, 0, 2), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(-2, 0, 1), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(1, 0, -2), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(2, 0, -1), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(-1, 0, 2), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(-2, 0, -1), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(-1, 0, -2), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(2, 0, 2), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(-2, 0, 2), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(-2, 0, -2), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(2, 0, -2), context.getConfig().leafState, 2);

        context.getWorld().setBlockState(pos.add(3, 0, 0), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(0, 0, 3), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(-3, 0, 0), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(0, 0, -3), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(3, 0, 1), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(1, 0, 3), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(-3, 0, 1), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(1, 0, -3), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(3, 0, -1), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(-1, 0, 3), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(-3, 0, -1), context.getConfig().leafState, 2);
        context.getWorld().setBlockState(pos.add(-1, 0, -3), context.getConfig().leafState, 2);

        if (!beSquare) {
            context.getWorld().setBlockState(pos.add(4, 0, 0), context.getConfig().leafState, 2);
            context.getWorld().setBlockState(pos.add(0, 0, 4), context.getConfig().leafState, 2);
            context.getWorld().setBlockState(pos.add(-4, 0, 0), context.getConfig().leafState, 2);
            context.getWorld().setBlockState(pos.add(0, 0, -4), context.getConfig().leafState, 2);
        }
    }
}
