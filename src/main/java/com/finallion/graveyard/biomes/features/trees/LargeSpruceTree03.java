package com.finallion.graveyard.biomes.features.trees;

import com.finallion.graveyard.biomes.features.surfaceFeatures.FeatureHelper;
import com.finallion.graveyard.biomes.features.trees.config.TGTreeFeatureConfig;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class LargeSpruceTree03 extends BaseSpruceTree {
    private final int trunkHeight = 25;


    public LargeSpruceTree03(Codec<TGTreeFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<TGTreeFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos blockPos = context.getOrigin();
        BlockPos.Mutable mutable = new BlockPos.Mutable().set(blockPos);
        BlockState wood = context.getConfig().woodState;
        BlockState leaf = context.getConfig().leafState;
        int offsetTrunk = context.getRandom().nextInt(3);

        if (!FeatureHelper.canBePlaced(world, blockPos.down(), world.getBlockState(blockPos.down()))) {
            return false;
        }

        if (!FeatureHelper.canGenerate(world, blockPos, 25)) {
            return false;
        }

        for (int i = 0; i < trunkHeight + offsetTrunk; i++) {
            world.setBlockState(blockPos.up(i), wood, 2);
            mutable.move(0, 1, 0);
        }

        // single tree top
        setLeaves(context, mutable.add(0, 4, 0), leaf);
        setLeaves(context, mutable.add(0, 3, 0), leaf);
        setLeaves(context, mutable.add(0, 2, 0), leaf);
        setLeaves(context, mutable.add(0, 1, 0), leaf);
        randomSpreadOne(context, mutable.add(0, 1, 0), false, 4);
        setLeaves(context, mutable.add(0, 0, 0), leaf);
        generateOneStar(context, mutable.add(0, 0, 0), false);
        randomSpreadOne(context, mutable.add(0, -1, 0), false, 2);
        randomSpreadTwo(context, mutable.add(0, -2, 0), true, 2);
        randomSpreadOne(context, mutable.add(0, -3, 0), false, 2);
        generateTwoStar(context, mutable.add(0, -4, 0), false);
        randomSpreadThree(context, mutable.add(0, -5, 0), true, 2);
        randomSpreadThree(context, mutable.add(0, -6, 0), false, 2);
        generateTwoStar(context, mutable.add(0, -7, 0), false);
        generateThreeStar(context, mutable.add(0, -8, 0), true);
        randomSpreadThree(context, mutable.add(0, -9, 0), true, 2);
        randomSpreadThree(context, mutable.add(0, -10, 0), false, 2);
        randomSpreadTwo(context, mutable.add(0, -11, 0), false, 2);

        generateThreeStar(context, mutable.add(0, -12, 0), false);
        randomSpreadFour(context, mutable.add(0, -13, 0), true, 2);
        randomSpreadFour(context, mutable.add(0, -14, 0), false, 2);
        generateTwoStar(context, mutable.add(0, -15, 0), false);

        randomSpreadFour(context, mutable.add(0, -12, 0), true, 2);
        randomSpreadFour(context, mutable.add(0, -13, 0), true, 4);
        randomSpreadFour(context, mutable.add(0, -14, 0), false, 2);
        generateTwoStar(context, mutable.add(0, -15, 0), false);

        generateFiveStar(context, mutable.add(0, -16, 0), true);
        randomSpreadFive(context, mutable.add(0, -17, 0), false, 2);
        randomSpreadFour(context, mutable.add(0, -18, 0), false, 2);

        generateFiveStar(context, mutable.add(0, -19, 0), false);
        randomSpreadFive(context, mutable.add(0, -20, 0), false, 2);
        randomSpreadFour(context, mutable.add(0, -21, 0), false, 2);

        randomSpreadFive(context, mutable.add(0, -22, 0), false, 2);
        generateFiveStar(context, mutable.add(0, -23, 0), false);
        randomSpreadFour(context, mutable.add(0, -24, 0), false, 2);
        return false;
    }
}
