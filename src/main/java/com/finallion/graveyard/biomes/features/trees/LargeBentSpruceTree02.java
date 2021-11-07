package com.finallion.graveyard.biomes.features.trees;

import com.finallion.graveyard.biomes.features.surfaceFeatures.FeatureHelper;
import com.finallion.graveyard.biomes.features.trees.config.TGTreeFeatureConfig;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Random;

public class LargeBentSpruceTree02 extends BaseSpruceTree {
    private final int trunkHeight = 25;

    public LargeBentSpruceTree02(Codec<TGTreeFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<TGTreeFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos blockPos = context.getOrigin();
        BlockPos.Mutable mutable = new BlockPos.Mutable().set(blockPos);
        BlockState wood = context.getConfig().woodState;
        BlockState leaf = context.getConfig().leafState;
        Random random = context.getRandom();
        int offsetTrunk = random.nextInt(3);
        Direction.Axis axis = random.nextBoolean() ? Direction.Axis.X : Direction.Axis.Z;
        Direction direction = Direction.from(axis, random.nextBoolean() ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE);


        if (!FeatureHelper.canBePlaced(world.getBlockState(blockPos.down()))) {
            return false;
        }

        if (!FeatureHelper.canGenerate(world, blockPos, 25)) {
            return false;
        }


        // straight half of the trunk
        // trunkHeight - top bent - middle bent + offset
        for (int i = 0; i <= trunkHeight - 10 - 8 + offsetTrunk; i++) {
            world.setBlockState(blockPos.up(i), wood, 2);
        }

        // bent first part of the trunk
        // trunkHeight - top bent + offset
        for (int i = 10 + offsetTrunk; i < trunkHeight - 7 + offsetTrunk; i++) {
            world.setBlockState(blockPos.up(i).offset(direction), wood, 2);
        }


        // bent second part of the trunk
        for (int i = 18 + offsetTrunk; i <= trunkHeight + offsetTrunk; i++) {
            world.setBlockState(blockPos.up(i).offset(direction, 2), wood, 2);
        }

        // start generating leaves from the top
        // shift leaf layer in bending direction
        mutable.move(0, trunkHeight + offsetTrunk, 0);
        mutable.move(direction, 2);

        // bent tree top leaf layer
        setLeaves(context, mutable.add(0, 6, 0).offset(direction, 2), leaf);
        setLeaves(context, mutable.add(0, 5, 0).offset(direction, 2), leaf);
        setLeaves(context, mutable.add(0, 5, 0).offset(direction), leaf);
        setLeaves(context, mutable.add(0, 4, 0).offset(direction), leaf);
        setLeaves(context, mutable.add(0, 3, 0).offset(direction), leaf);
        setLeaves(context, mutable.add(0, 3, 0), leaf);
        setLeaves(context, mutable.add(0, 2, 0), leaf);
        setLeaves(context, mutable.add(0, 1, 0), leaf);
        setLeaves(context, mutable.add(0, 2, 0).offset(direction.getOpposite()), leaf);

        generateOneStar(context, mutable.add(0, 1, 0), false);
        generateTwoStar(context, mutable.add(0, 0, 0), true);
        generateOneStar(context, mutable.add(0, -1, 0), false);
        randomSpreadOne(context, mutable.add(0, -2, 0), false, 2);
        generateTwoStar(context, mutable.add(0, -3, 0), false);
        randomSpreadThree(context, mutable.add(0, -3, 0), true, 2);
        randomSpreadTwo(context, mutable.add(0, -4, 0), false, 2);
        generateOneStar(context, mutable.add(0, -5, 0), false);
        generateTwoStar(context, mutable.add(0, -6, 0), false);
        generateThreeStar(context, mutable.add(0, -7, 0), true);

        // middle bent
        mutable.move(direction.getOpposite(), 1);
        randomSpreadThree(context, mutable.add(0, -7, 0), true, 2);
        generateOneStar(context, mutable.add(0, -8, 0), false);
        randomSpreadTwo(context, mutable.add(0, -8, 0), false, 4);
        generateTwoStar(context, mutable.add(0, -9, 0), false);
        generateThreeStar(context, mutable.add(0, -10, 0), false);
        randomSpreadTwo(context, mutable.add(0, -11, 0), true, 2);
        generateOneStar(context, mutable.add(0, -12, 0), false);
        randomSpreadTwo(context, mutable.add(0, -12, 0), false, 4);
        generateTwoStar(context, mutable.add(0, -13, 0), false);

        // lower bent
        mutable.move(direction.getOpposite(), 1);
        generateTwoStar(context, mutable.add(0, -13, 0), false);
        randomSpreadFour(context, mutable.add(0, -13, 0), false, 4);
        generateThreeStar(context, mutable.add(0, -14, 0), false);
        randomSpreadFour(context, mutable.add(0, -14, 0), false, 4);
        generateFourStar(context, mutable.add(0, -15, 0), true);
        randomSpreadFour(context, mutable.add(0, -15, 0), false, 4);
        generateTwoStar(context, mutable.add(0, -16, 0), false);
        randomSpreadFour(context, mutable.add(0, -16, 0), false, 8);
        generateThreeStar(context, mutable.add(0, -17, 0), false);
        randomSpreadFour(context, mutable.add(0, -17, 0), false, 4);
        generateFourStar(context, mutable.add(0, -18, 0), false);
        randomSpreadFive(context, mutable.add(0, -18, 0), false, 4);
        generateFiveStar(context, mutable.add(0, -19, 0), true);
        generateThreeStar(context, mutable.add(0, -20, 0), false);
        randomSpreadTwo(context, mutable.add(0, -21, 0), false, 4);
        generateOneStar(context, mutable.add(0, -22, 0), false);

        return false;
    }
}
