package com.finallion.graveyard.blocks.saplings;

import com.finallion.graveyard.init.TGConfiguredFeatures;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

import java.util.Random;

public class GraveyardSpruceTreeSaplingGenerator extends SaplingGenerator {

    @Override
    protected ConfiguredFeature<TreeFeatureConfig, ?> createTreeFeature(Random random, boolean bl) {
        int rand = random.nextInt(2);
        if (rand == 0) {
            return Feature.TREE.configure(TGConfiguredFeatures.SMALL_GRAVEYARD_SPRUCE_TREE);
        } else {
            return Feature.TREE.configure(TGConfiguredFeatures.LARGE_GRAVEYARD_SPRUCE_TREE);
        }
    }
}
