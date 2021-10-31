package com.finallion.graveyard.features;

import com.finallion.graveyard.mixin.FoliagePlacerTypeAccessor;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacerType;
import net.minecraft.world.gen.trunk.BendingTrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacer;

public class TreeFeatureConfig implements FeatureConfig {
    public static final FoliagePlacerType<GraveyardSpruceTreeFoliagePlacer> SPRUCE_FOLIAGE = FoliagePlacerTypeAccessor.createFoliagePlacerType(GraveyardSpruceTreeFoliagePlacer.CODEC);

    public static final ConfiguredFeature<?, ?> WILLOW_TREE = Feature.TREE.configure((new TreeFeatureConfig.Builder(
            new SimpleBlockStateProvider(BMBlocks.WILLOW_WOOD_INFO.getBlock(WoodTypeInfo.Type.LOG).getDefaultState()),
            new SimpleBlockStateProvider(BMBlocks.WILLOW_LEAVES.getDefaultState()),
            new WillowFoliagePlacer(UniformIntDistribution.of(1), UniformIntDistribution.of(1), 3, true),
            new BendingTrunkPlacer(12, 6, 0, 2, UniformIntProvider.create(1, 2)),
            new TwoLayersFeatureSize(1, 0, 1)).ignoreVines().maxWaterDepth(1).build()));

    public static final ConfiguredFeature<?, ?> WILLOW_TREES = WILLOW_TREE.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP).decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(2, 0.1F, 1)));

}
