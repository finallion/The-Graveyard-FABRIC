package com.finallion.graveyard.world.features.trees;

import com.finallion.graveyard.world.features.surfaceFeatures.FeatureHelper;
import com.finallion.graveyard.world.features.trees.config.TGTreeFeatureConfig;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class SmallSpruceTree01 extends BaseSpruceTree {
    private final int trunkHeight = 12;


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
        int offsetTrunk = context.getRandom().nextInt(3);

        if (!FeatureHelper.canBePlaced(world, blockPos.down(), world.getBlockState(blockPos.down()))) {
            return false;
        }

        if (!FeatureHelper.canGenerate(world, blockPos, 12)) {
            return false;
        }

        for (int i = 0; i < trunkHeight + offsetTrunk; i++) {
            world.setBlockState(blockPos.up(i), wood, 2);
            mutable.move(0, 1, 0);
        }

        // single tree top
        setLeaves(context, mutable.add(0, 3, 0), leaf);
        setLeaves(context, mutable.add(0, 2, 0), leaf);
        setLeaves(context, mutable.add(0, 1, 0), leaf);
        setLeaves(context, mutable.add(0, 0, 0), leaf);

        generateOneStar(context, mutable.add(0, 0, 0), false);
        randomSpreadTwo(context, mutable.add(0, -1, 0), true, 2);

        // second layer
        generateOneStar(context, mutable.add(0, -3, 0), false);
        generateTwoStar(context, mutable.add(0, -4, 0), false);
        randomSpreadTwo(context, mutable.add(0, -5, 0), false, 2);

        // third layer
        generateTwoStar(context, mutable.add(0, -6, 0), false);
        generateThreeStar(context, mutable.add(0, -7, 0), true);

        randomSpreadOne(context, mutable.add(0, -8, 0), false, 4);

        // forth layer
        generateTwoStar(context, mutable.add(0, -9, 0), false);
        generateThreeStar(context, mutable.add(0, -10, 0), false);


        return false;
    }
}
