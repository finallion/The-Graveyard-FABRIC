package com.finallion.graveyard.features;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

import java.util.Random;
import java.util.function.BiConsumer;

public class GraveyardSpruceTreeFoliagePlacer extends FoliagePlacer {
    public static final Codec<GraveyardSpruceTreeFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> FoliagePlacer.fillFoliagePlacerFields(instance).apply(instance, GraveyardSpruceTreeFoliagePlacer::new));

    public GraveyardSpruceTreeFoliagePlacer(IntProvider radius, IntProvider offset) {
        super(radius, offset);
    }


    @Override
    protected FoliagePlacerType<?> getType() {
        return com.finallion.graveyard.features.TreeFeatureConfig.SPRUCE_FOLIAGE;
    }

    @Override
    protected void generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, TreeFeatureConfig config, int trunkHeight, TreeNode treeNode, int foliageHeight, int radius, int offset) {

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
