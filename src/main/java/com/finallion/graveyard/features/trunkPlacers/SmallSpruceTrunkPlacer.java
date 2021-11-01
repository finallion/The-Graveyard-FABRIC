package com.finallion.graveyard.features.trunkPlacers;

import com.finallion.graveyard.init.TGConfiguredFeatures;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

public class SmallSpruceTrunkPlacer extends TrunkPlacer {
    public static final Codec<SmallSpruceTrunkPlacer> CODEC = RecordCodecBuilder.create(instance ->
            fillTrunkPlacerFields(instance).apply(instance, SmallSpruceTrunkPlacer::new));

    public SmallSpruceTrunkPlacer(int baseHeight, int firstRandomHeight, int secondRandomHeight) {
        super(baseHeight, firstRandomHeight, secondRandomHeight);
    }

    @Override
    protected TrunkPlacerType<?> getType() {
        return TGConfiguredFeatures.SMALL_SPRUCE_TRUNK;
    }

    @Override
    public List<FoliagePlacer.TreeNode> generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, int height, BlockPos startPos, TreeFeatureConfig config) {
        // set the ground beneath the trunk to dirt
        this.setToDirt(world, replacer, random, startPos.down(), config);

        for (int i = 0; i < height; i++) {
            this.getAndSetState(world, replacer, random, startPos.up(i), config);
        }


        return ImmutableList.of(new FoliagePlacer.TreeNode(startPos.up(height), 0, false));
    }
}
