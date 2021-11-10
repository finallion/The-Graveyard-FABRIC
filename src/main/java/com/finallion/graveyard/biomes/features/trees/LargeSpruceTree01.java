package com.finallion.graveyard.biomes.features.trees;

import com.finallion.graveyard.biomes.features.surfaceFeatures.FeatureHelper;
import com.finallion.graveyard.biomes.features.trees.config.TGTreeFeatureConfig;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class LargeSpruceTree01 extends BaseSpruceTree {
    private final int trunkHeight = 18;


    public LargeSpruceTree01(Codec<TGTreeFeatureConfig> configCodec) {
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

        if (!FeatureHelper.canBePlaced(world.getBlockState(blockPos.down()))) {
            return false;
        }

        if (!FeatureHelper.canGenerate(world, blockPos, 18)) {
            return false;
        }

        for (int i = 0; i < trunkHeight + offsetTrunk; i++) {
            world.setBlockState(blockPos.up(i), wood, 2);
            mutable.move(0, 1, 0);
        }

        // single tree top
        setLeaves(context, mutable.add(0, 2, 0), leaf);
        setLeaves(context, mutable.add(0, 1, 0), leaf);
        setLeaves(context, mutable.add(0, 0, 0), leaf);

        randomSpreadTwo(context, mutable.add(0, -1, 0), true, 2);
        randomSpreadOne(context, mutable.add(0, -2, 0), false, 2);
        generateOneStar(context, mutable.add(0, -3, 0), false);
        randomSpreadTwo(context, mutable.add(0, -3, 0), false, 5);
        randomSpreadTwo(context, mutable.add(0, -4, 0), false, 2);
        randomSpreadTwo(context, mutable.add(0, -5, 0), false, 2);
        generateTwoStar(context, mutable.add(0, -6, 0), false);
        randomSpreadThree(context, mutable.add(0, -6, 0), true, 4);
        generateBranchesOne(context, mutable.add(0, -6, 0), 1);
        randomSpreadTwo(context, mutable.add(0, -7, 0), false, 2);
        generateOneStar(context, mutable.add(0, -8, 0), false);
        randomSpreadTwo(context, mutable.add(0, -8, 0), false, 2);
        generateTwoStar(context, mutable.add(0, -9, 0), false);
        generateTwoStar(context, mutable.add(0, -10, 0), false);
        randomSpreadThree(context, mutable.add(0, -10, 0), false, 4);
        generateBranchesOne(context, mutable.add(0, -10, 0), 1);
        generateThreeStar(context, mutable.add(0, -11, 0), true);
        generateFourStar(context, mutable.add(0, -12, 0), true);
        randomSpreadFour(context, mutable.add(0, -12, 0), false, 4);
        generateFourStar(context, mutable.add(0, -13, 0), false);
        randomSpreadFour(context, mutable.add(0, -13, 0), false, 4);
        generateFourStar(context, mutable.add(0, -14, 0), false);
        generateBranchesOne(context, mutable.add(0, -14, 0), 1);
        generateFourStar(context, mutable.add(0, -15, 0), true);
        generateTwoStar(context, mutable.add(0, -16, 0), true);
        return false;
    }
}
