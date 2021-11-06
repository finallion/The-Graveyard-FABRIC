package com.finallion.graveyard.init;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.biomes.features.MoosCarpetFeature;
import com.finallion.graveyard.biomes.features.TGTreeFeatureConfig;
import com.finallion.graveyard.biomes.features.trees.FallenSpruceTree;
import com.finallion.graveyard.biomes.features.trees.SmallBentSpruceTree01;
import com.finallion.graveyard.biomes.features.trees.SmallSpruceTree01;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.CountConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.HeightmapDecoratorConfig;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.FoliagePlacerType;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

public class TGConfiguredFeatures {

    // tree features
    public static final Feature<TGTreeFeatureConfig> SMALL_SPRUCE_TREE_01 = new SmallSpruceTree01(TGTreeFeatureConfig.CODEC);
    public static final Feature<TGTreeFeatureConfig> SMALL_BENT_SPRUCE_TREE_01 = new SmallBentSpruceTree01(TGTreeFeatureConfig.CODEC);
    public static final Feature<TGTreeFeatureConfig> FALLEN_SPRUCE_TREE = new FallenSpruceTree(TGTreeFeatureConfig.CODEC);


    // other features
    private static final Feature<DefaultFeatureConfig> MOSS_CARPET_FEATURE = new MoosCarpetFeature(DefaultFeatureConfig.CODEC);

    // configured features
    public static final RegistryKey<ConfiguredFeature<?, ?>> MOSS_CARPET_FEATURE_KEY = RegistryKey.of(Registry.CONFIGURED_FEATURE_KEY, new Identifier(TheGraveyard.MOD_ID, "moss_carpet_feature"));

    public static final ConfiguredFeature<?, ?> MOSS_CARPET_CONFIG = MOSS_CARPET_FEATURE.configure(new DefaultFeatureConfig())
            .decorate(Decorator.HEIGHTMAP.configure(new HeightmapDecoratorConfig(Heightmap.Type.OCEAN_FLOOR_WG))).repeat(50);


    // configured tree feature collections
    public static final ConfiguredFeature<?, ?> HAUNTED_FOREST_TREES = register("haunted_forest_trees", Feature.RANDOM_SELECTOR.configure(
            new RandomFeatureConfig(
                    ImmutableList.of(
                            TGConfiguredFeatures.SMALL_SPRUCE_TREE_01.configure(new TGTreeFeatureConfig(Blocks.STRIPPED_SPRUCE_LOG.getDefaultState(), Blocks.SPRUCE_LEAVES.getDefaultState())).withChance(0.5F),
                            TGConfiguredFeatures.SMALL_BENT_SPRUCE_TREE_01.configure(new TGTreeFeatureConfig(Blocks.STRIPPED_SPRUCE_LOG.getDefaultState(), Blocks.SPRUCE_LEAVES.getDefaultState())).withChance(0.5F),
                            TGConfiguredFeatures.FALLEN_SPRUCE_TREE.configure(new TGTreeFeatureConfig(Blocks.STRIPPED_SPRUCE_LOG.getDefaultState(), Blocks.SPRUCE_LEAVES.getDefaultState())).withChance(0.1F)
                            ),
                    ConfiguredFeatures.SPRUCE))
            .decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
            .decorate(Decorator.COUNT.configure(new CountConfig(7))));





    private static <FC extends FeatureConfig> ConfiguredFeature<FC, ?> register(String id, ConfiguredFeature<FC, ?> feature) {
        return Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier(TheGraveyard.MOD_ID, id), feature);
    }

    public static void registerFeatures() {
        Registry.register(Registry.FEATURE, new Identifier(TheGraveyard.MOD_ID, "moss_carpet_feature"), MOSS_CARPET_FEATURE);
        Registry.register(Registry.FEATURE, new Identifier(TheGraveyard.MOD_ID, "small_spruce_tree_01"), SMALL_SPRUCE_TREE_01);
        Registry.register(Registry.FEATURE, new Identifier(TheGraveyard.MOD_ID, "small_bent_spruce_tree_01"), SMALL_BENT_SPRUCE_TREE_01);
        Registry.register(Registry.FEATURE, new Identifier(TheGraveyard.MOD_ID, "fallen_spruce_tree"), FALLEN_SPRUCE_TREE);
    }

    public static void registerConfiguredFeatures() {
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, MOSS_CARPET_FEATURE_KEY.getValue(), MOSS_CARPET_CONFIG);
    }
}
