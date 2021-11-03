package com.finallion.graveyard.biomes.features.foliagePlacers;

import com.finallion.graveyard.init.TGConfiguredFeatures;
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

public class SmallSpruceFoliagePlacer extends TGFoliagePlacer {
    public static final Codec<SmallSpruceFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> fillFoliagePlacerFields(instance)
            .and(IntProvider.createValidatingCodec(1, 512).fieldOf("foliageHeight").forGetter(SmallSpruceFoliagePlacer::getFoliageHeight))
            .apply(instance, SmallSpruceFoliagePlacer::new));

    private final IntProvider foliageHeight;

    public SmallSpruceFoliagePlacer(IntProvider radius, IntProvider offset, IntProvider foliageHeight) {
        super(radius, offset);
        this.foliageHeight = foliageHeight;
    }


    @Override
    protected FoliagePlacerType<?> getType() {
        return TGConfiguredFeatures.SMALL_SPRUCE_FOLIAGE;
    }

    @Override
    protected void generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, TreeFeatureConfig config, int trunkHeight, TreeNode treeNode, int foliageHeight, int radius, int offset) {
        BlockPos.Mutable blockPos = new BlockPos.Mutable().set(treeNode.getCenter());

        // single tree top
        placeFoliageBlock(world, replacer, random, config, blockPos.add(0, 3, 0));
        placeFoliageBlock(world, replacer, random, config, blockPos.add(0, 2, 0));
        placeFoliageBlock(world, replacer, random, config, blockPos.add(0, 1, 0));
        placeFoliageBlock(world, replacer, random, config, blockPos.add(0, 0, 0));

        generateOneStar(world, replacer, random, config, blockPos, -0);
        generateOneStar(world, replacer, random, config, blockPos, -1);

        // second layer
        generateOneStar(world, replacer, random, config, blockPos, -3);
        generateTwoStar(world, replacer, random, config, blockPos, -4, false);

        placeFoliageBlock(world, replacer, random, config, blockPos.add(1, -5, 0));
        placeFoliageBlock(world, replacer, random, config, blockPos.add(0, -5, 1));
        placeFoliageBlock(world, replacer, random, config, blockPos.add(-1, -5, -1));

        // third layer
        generateTwoStar(world, replacer, random, config, blockPos, -6, false);
        generateThreeStar(world, replacer, random, config, blockPos, -7, true);

        placeFoliageBlock(world, replacer, random, config, blockPos.add(1, -8, 0));
        placeFoliageBlock(world, replacer, random, config, blockPos.add(0, -8, 1));
        placeFoliageBlock(world, replacer, random, config, blockPos.add(0, -8, -1));

        // forth layer
        generateTwoStar(world, replacer, random, config, blockPos, -9, false);
        generateThreeStar(world, replacer, random, config, blockPos, -10, false);




    }


    public IntProvider getFoliageHeight() {
        return this.foliageHeight;
    }

    @Override
    public int getRandomHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
        return foliageHeight.get(random);
    }

    @Override
    protected boolean isInvalidForLeaves(Random random, int dx, int y, int dz, int radius, boolean giantTrunk) {
        return dx == radius && dz == radius && radius > 0;
    }


}
