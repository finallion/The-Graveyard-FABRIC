package com.finallion.graveyard.features.foliagePlacers;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

import java.util.Random;
import java.util.function.BiConsumer;

public abstract class TGFoliagePlacer extends FoliagePlacer {

    public TGFoliagePlacer(IntProvider radius, IntProvider offset) {
        super(radius, offset);
    }

    @Override
    protected FoliagePlacerType<?> getType() {
        return null;
    }

    @Override
    protected void generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, TreeFeatureConfig config, int trunkHeight, TreeNode treeNode, int foliageHeight, int radius, int offset) {

    }

    public void generateOneStar(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, TreeFeatureConfig config, BlockPos.Mutable pos, int height) {
        /*
                #
              # o #
                #
         */
        placeFoliageBlock(world, replacer, random, config, pos.add(1, height, 0));
        placeFoliageBlock(world, replacer, random, config, pos.add(0, height, 1));
        placeFoliageBlock(world, replacer, random, config, pos.add(-1, height, 0));
        placeFoliageBlock(world, replacer, random, config, pos.add(0, height, -1));

    }


    public void generateTwoStar(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, TreeFeatureConfig config, BlockPos.Mutable pos, int height, boolean shouldBeSquare) {
        /*
                #
              # # #
            # # o # #
              # # #
                #
         */

        placeFoliageBlock(world, replacer, random, config, pos.add(1, height, 0));
        placeFoliageBlock(world, replacer, random, config, pos.add(0, height, 1));
        placeFoliageBlock(world, replacer, random, config, pos.add(-1, height, 0));
        placeFoliageBlock(world, replacer, random, config, pos.add(0, height, -1));
        placeFoliageBlock(world, replacer, random, config, pos.add(-1, height, -1));
        placeFoliageBlock(world, replacer, random, config, pos.add(1, height, 1));
        placeFoliageBlock(world, replacer, random, config, pos.add(-1, height, 1));
        placeFoliageBlock(world, replacer, random, config, pos.add(1, height, -1));

        if (!shouldBeSquare) {
            placeFoliageBlock(world, replacer, random, config, pos.add(2, height, 0));
            placeFoliageBlock(world, replacer, random, config, pos.add(0, height, 2));
            placeFoliageBlock(world, replacer, random, config, pos.add(-2, height, 0));
            placeFoliageBlock(world, replacer, random, config, pos.add(0, height, -2));
        }
    }



    public void generateThreeStar(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, TreeFeatureConfig config, BlockPos.Mutable pos, int height, boolean shouldBeSquare) {
        /*
                #
              # # #
            # # # # #
          # # # o # # #
            # # # # #
              # # #
                #
         */

        placeFoliageBlock(world, replacer, random, config, pos.add(1, height, 0));
        placeFoliageBlock(world, replacer, random, config, pos.add(0, height, 1));
        placeFoliageBlock(world, replacer, random, config, pos.add(-1, height, 0));
        placeFoliageBlock(world, replacer, random, config, pos.add(0, height, -1));
        placeFoliageBlock(world, replacer, random, config, pos.add(-1, height, -1));
        placeFoliageBlock(world, replacer, random, config, pos.add(1, height, 1));
        placeFoliageBlock(world, replacer, random, config, pos.add(-1, height, 1));
        placeFoliageBlock(world, replacer, random, config, pos.add(1, height, -1));
        placeFoliageBlock(world, replacer, random, config, pos.add(2, height, 0));
        placeFoliageBlock(world, replacer, random, config, pos.add(0, height, 2));
        placeFoliageBlock(world, replacer, random, config, pos.add(-2, height, 0));
        placeFoliageBlock(world, replacer, random, config, pos.add(0, height, -2));
        placeFoliageBlock(world, replacer, random, config, pos.add(2, height, 1));
        placeFoliageBlock(world, replacer, random, config, pos.add(1, height, 2));
        placeFoliageBlock(world, replacer, random, config, pos.add(-2, height, 1));
        placeFoliageBlock(world, replacer, random, config, pos.add(1, height, -2));
        placeFoliageBlock(world, replacer, random, config, pos.add(2, height, -1));
        placeFoliageBlock(world, replacer, random, config, pos.add(-1, height, 2));
        placeFoliageBlock(world, replacer, random, config, pos.add(-2, height, -1));
        placeFoliageBlock(world, replacer, random, config, pos.add(-1, height, -2));

        if (!shouldBeSquare) {
            placeFoliageBlock(world, replacer, random, config, pos.add(3, height, 0));
            placeFoliageBlock(world, replacer, random, config, pos.add(0, height, 3));
            placeFoliageBlock(world, replacer, random, config, pos.add(-3, height, 0));
            placeFoliageBlock(world, replacer, random, config, pos.add(0, height, -3));
        }

    }


    public void generateFourStar(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, TreeFeatureConfig config, BlockPos.Mutable pos, int height, boolean shouldBeSquare) {
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

        placeFoliageBlock(world, replacer, random, config, pos.add(1, height, 0));
        placeFoliageBlock(world, replacer, random, config, pos.add(0, height, 1));
        placeFoliageBlock(world, replacer, random, config, pos.add(-1, height, 0));
        placeFoliageBlock(world, replacer, random, config, pos.add(0, height, -1));
        placeFoliageBlock(world, replacer, random, config, pos.add(-1, height, -1));
        placeFoliageBlock(world, replacer, random, config, pos.add(1, height, 1));
        placeFoliageBlock(world, replacer, random, config, pos.add(-1, height, 1));
        placeFoliageBlock(world, replacer, random, config, pos.add(1, height, -1));

        placeFoliageBlock(world, replacer, random, config, pos.add(2, height, 0));
        placeFoliageBlock(world, replacer, random, config, pos.add(0, height, 2));
        placeFoliageBlock(world, replacer, random, config, pos.add(-2, height, 0));
        placeFoliageBlock(world, replacer, random, config, pos.add(0, height, -2));
        placeFoliageBlock(world, replacer, random, config, pos.add(2, height, 1));
        placeFoliageBlock(world, replacer, random, config, pos.add(1, height, 2));
        placeFoliageBlock(world, replacer, random, config, pos.add(-2, height, 1));
        placeFoliageBlock(world, replacer, random, config, pos.add(1, height, -2));
        placeFoliageBlock(world, replacer, random, config, pos.add(2, height, -1));
        placeFoliageBlock(world, replacer, random, config, pos.add(-1, height, 2));
        placeFoliageBlock(world, replacer, random, config, pos.add(-2, height, -1));
        placeFoliageBlock(world, replacer, random, config, pos.add(-1, height, -2));
        placeFoliageBlock(world, replacer, random, config, pos.add(2, height, 2));
        placeFoliageBlock(world, replacer, random, config, pos.add(-2, height, 2));
        placeFoliageBlock(world, replacer, random, config, pos.add(-2, height, -2));
        placeFoliageBlock(world, replacer, random, config, pos.add(2, height, -2));

        placeFoliageBlock(world, replacer, random, config, pos.add(3, height, 0));
        placeFoliageBlock(world, replacer, random, config, pos.add(0, height, 3));
        placeFoliageBlock(world, replacer, random, config, pos.add(-3, height, 0));
        placeFoliageBlock(world, replacer, random, config, pos.add(0, height, -3));
        placeFoliageBlock(world, replacer, random, config, pos.add(3, height, 1));
        placeFoliageBlock(world, replacer, random, config, pos.add(1, height, 3));
        placeFoliageBlock(world, replacer, random, config, pos.add(-3, height, 1));
        placeFoliageBlock(world, replacer, random, config, pos.add(1, height, -3));
        placeFoliageBlock(world, replacer, random, config, pos.add(3, height, -1));
        placeFoliageBlock(world, replacer, random, config, pos.add(-1, height, 3));
        placeFoliageBlock(world, replacer, random, config, pos.add(-3, height, -1));
        placeFoliageBlock(world, replacer, random, config, pos.add(-1, height, -3));

        if (!shouldBeSquare) {
            placeFoliageBlock(world, replacer, random, config, pos.add(4, height, 0));
            placeFoliageBlock(world, replacer, random, config, pos.add(0, height, 4));
            placeFoliageBlock(world, replacer, random, config, pos.add(-4, height, 0));
            placeFoliageBlock(world, replacer, random, config, pos.add(0, height, -4));
        }
    }

    @Override
    public int getRandomHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
        return 0;
    }

    @Override
    protected boolean isInvalidForLeaves(Random random, int dx, int y, int dz, int radius, boolean giantTrunk) {
        return false;
    }
}
