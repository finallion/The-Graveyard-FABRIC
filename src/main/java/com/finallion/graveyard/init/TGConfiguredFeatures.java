package com.finallion.graveyard.init;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.biomes.features.foliagePlacers.SmallSpruceFoliagePlacer;
import com.finallion.graveyard.biomes.features.trunkPlacers.LargeSpruceTrunkPlacer;
import com.finallion.graveyard.biomes.features.trunkPlacers.SmallSpruceTrunkPlacer;
import com.finallion.graveyard.mixin.FoliagePlacerTypeAccessor;
import com.finallion.graveyard.mixin.TrunkPlacerTypeAccessor;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.CountConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.FoliagePlacerType;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

public class TGConfiguredFeatures {
    public static final FoliagePlacerType<SmallSpruceFoliagePlacer> SMALL_SPRUCE_FOLIAGE = FoliagePlacerTypeAccessor.callRegister("small_spruce_foliage_placer", SmallSpruceFoliagePlacer.CODEC);
    public static final TrunkPlacerType<SmallSpruceTrunkPlacer> SMALL_SPRUCE_TRUNK = TrunkPlacerTypeAccessor.callRegister("small_spruce_trunk_placer", SmallSpruceTrunkPlacer.CODEC);
    public static final TrunkPlacerType<LargeSpruceTrunkPlacer> LARGE_SPRUCE_TRUNK = TrunkPlacerTypeAccessor.callRegister("large_spruce_trunk_placer", LargeSpruceTrunkPlacer.CODEC);


    public static TreeFeatureConfig SMALL_GRAVEYARD_SPRUCE_TREE = new TreeFeatureConfig.Builder(
            new SimpleBlockStateProvider(Blocks.STRIPPED_SPRUCE_LOG.getDefaultState()),
            //new BendingTrunkPlacer(15, 0, 15, 3, UniformIntProvider.create(0, 0)),
            new SmallSpruceTrunkPlacer(12, 0, 0),
            new SimpleBlockStateProvider(Blocks.SPRUCE_LEAVES.getDefaultState()),
            new SimpleBlockStateProvider(TGBlocks.SMALL_GRAVEYARD_SPRUCE_SAPLING.getDefaultState()),
            new SmallSpruceFoliagePlacer(ConstantIntProvider.create(5), ConstantIntProvider.create(0), ConstantIntProvider.create(3)),
            //new BlobFoliagePlacer(ConstantIntProvider.create(5), ConstantIntProvider.create(0), 3),
            new TwoLayersFeatureSize(1, 0, 1)).ignoreVines()
            .build();

    public static TreeFeatureConfig LARGE_GRAVEYARD_SPRUCE_TREE = new TreeFeatureConfig.Builder(
            new SimpleBlockStateProvider(Blocks.STRIPPED_SPRUCE_LOG.getDefaultState()),
            //new BendingTrunkPlacer(15, 0, 15, 3, UniformIntProvider.create(0, 0)),
            new SmallSpruceTrunkPlacer(24, 0, 0),
            new SimpleBlockStateProvider(Blocks.SPRUCE_LEAVES.getDefaultState()),
            new SimpleBlockStateProvider(TGBlocks.SMALL_GRAVEYARD_SPRUCE_SAPLING.getDefaultState()),
            new SmallSpruceFoliagePlacer(ConstantIntProvider.create(6), ConstantIntProvider.create(0), ConstantIntProvider.create(3)),
            //new BlobFoliagePlacer(ConstantIntProvider.create(5), ConstantIntProvider.create(0), 3),
            new TwoLayersFeatureSize(1, 0, 1)).ignoreVines()
            .build();




    public static final ConfiguredFeature<?, ?> HAUNTED_FOREST_TREES = register("haunted_forest_trees", Feature.RANDOM_SELECTOR.configure(
            new RandomFeatureConfig(
                    ImmutableList.of(
                            Feature.TREE.configure(SMALL_GRAVEYARD_SPRUCE_TREE).withChance(0.6F),
                            Feature.TREE.configure(LARGE_GRAVEYARD_SPRUCE_TREE).withChance(0.6F)
                    ), ConfiguredFeatures.SPRUCE))
            .decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
            .decorate(Decorator.COUNT.configure(new CountConfig(7))));





    private static <FC extends FeatureConfig> ConfiguredFeature<FC, ?> register(String id, ConfiguredFeature<FC, ?> feature) {
        return Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier(TheGraveyard.MOD_ID, id), feature);
    }
}
