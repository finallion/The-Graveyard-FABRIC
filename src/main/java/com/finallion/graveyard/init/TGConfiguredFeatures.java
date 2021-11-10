package com.finallion.graveyard.init;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.biomes.features.surfaceFeatures.*;
import com.finallion.graveyard.biomes.features.trees.*;
import com.finallion.graveyard.biomes.features.trees.config.TGTreeFeatureConfig;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.CountConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.HeightmapDecoratorConfig;
import net.minecraft.world.gen.feature.*;

public class TGConfiguredFeatures {


    // tree features
    public static final Feature<TGTreeFeatureConfig> SMALL_SPRUCE_TREE_01 = new SmallSpruceTree01(TGTreeFeatureConfig.CODEC);
    public static final Feature<TGTreeFeatureConfig> SMALL_SPRUCE_TREE_02 = new SmallSpruceTree02(TGTreeFeatureConfig.CODEC);
    public static final Feature<TGTreeFeatureConfig> SMALL_SPRUCE_TREE_03 = new SmallSpruceTree03(TGTreeFeatureConfig.CODEC);
    public static final Feature<TGTreeFeatureConfig> SMALL_SPRUCE_TREE_04 = new SmallSpruceTree04(TGTreeFeatureConfig.CODEC);
    public static final Feature<TGTreeFeatureConfig> SMALL_SPRUCE_TREE_05 = new SmallSpruceTree05(TGTreeFeatureConfig.CODEC);
    public static final Feature<TGTreeFeatureConfig> SMALL_SPRUCE_TREE_06 = new SmallSpruceTree06(TGTreeFeatureConfig.CODEC);
    public static final Feature<TGTreeFeatureConfig> SMALL_BENT_SPRUCE_TREE_01 = new SmallBentSpruceTree01(TGTreeFeatureConfig.CODEC);
    public static final Feature<TGTreeFeatureConfig> FALLEN_SPRUCE_TREE = new FallenSpruceTree(TGTreeFeatureConfig.CODEC);
    public static final Feature<TGTreeFeatureConfig> LARGE_BENT_SPRUCE_TREE_01 = new LargeBentSpruceTree01(TGTreeFeatureConfig.CODEC);
    public static final Feature<TGTreeFeatureConfig> LARGE_BENT_SPRUCE_TREE_02 = new LargeBentSpruceTree02(TGTreeFeatureConfig.CODEC);
    public static final Feature<TGTreeFeatureConfig> LARGE_SPRUCE_TREE_01 = new LargeSpruceTree01(TGTreeFeatureConfig.CODEC);
    public static final Feature<TGTreeFeatureConfig> LARGE_SPRUCE_TREE_02 = new LargeSpruceTree02(TGTreeFeatureConfig.CODEC);
    public static final Feature<TGTreeFeatureConfig> LARGE_SPRUCE_TREE_03 = new LargeSpruceTree03(TGTreeFeatureConfig.CODEC);

    // other features
    private static final Feature<DefaultFeatureConfig> MOSS_CARPET_FEATURE = new MossCarpetFeature(DefaultFeatureConfig.CODEC);
    private static final Feature<DefaultFeatureConfig> COBWEB_FEATURE = new CobwebFeature(DefaultFeatureConfig.CODEC);
    private static final Feature<DefaultFeatureConfig> BUSH_FEATURE = new BushFeature(DefaultFeatureConfig.CODEC);
    private static final Feature<DefaultFeatureConfig> MOSSY_BOULDER_FEATURE = new MossyBoulderFeature(DefaultFeatureConfig.CODEC);
    private static final Feature<DefaultFeatureConfig> GRAVESTONE_FEATURE = new GraveFeature(DefaultFeatureConfig.CODEC);
    private static final Feature<DefaultFeatureConfig> SOUL_LIGHT_FEATURE = new SoulLightFeature(DefaultFeatureConfig.CODEC);

    // configured features registry keys
    public static final RegistryKey<ConfiguredFeature<?, ?>> MOSS_CARPET_FEATURE_KEY = RegistryKey.of(Registry.CONFIGURED_FEATURE_KEY, new Identifier(TheGraveyard.MOD_ID, "moss_carpet_feature"));
    public static final RegistryKey<ConfiguredFeature<?, ?>> COBWEB_FEATURE_KEY = RegistryKey.of(Registry.CONFIGURED_FEATURE_KEY, new Identifier(TheGraveyard.MOD_ID, "cobweb_feature"));
    public static final RegistryKey<ConfiguredFeature<?, ?>> BUSH_FEATURE_KEY = RegistryKey.of(Registry.CONFIGURED_FEATURE_KEY, new Identifier(TheGraveyard.MOD_ID, "bush_feature"));
    public static final RegistryKey<ConfiguredFeature<?, ?>> MOSSY_BOULDER_FEATURE_KEY = RegistryKey.of(Registry.CONFIGURED_FEATURE_KEY, new Identifier(TheGraveyard.MOD_ID, "mossy_boulder_feature"));
    public static final RegistryKey<ConfiguredFeature<?, ?>> GRAVESTONE_FEATURE_KEY = RegistryKey.of(Registry.CONFIGURED_FEATURE_KEY, new Identifier(TheGraveyard.MOD_ID, "gravestone_feature"));
    public static final RegistryKey<ConfiguredFeature<?, ?>> SOUL_LIGHT_FEATURE_KEY = RegistryKey.of(Registry.CONFIGURED_FEATURE_KEY, new Identifier(TheGraveyard.MOD_ID, "soul_light_feature"));

    // configured features
    public static final ConfiguredFeature<?, ?> MOSS_CARPET_CONFIG = MOSS_CARPET_FEATURE.configure(new DefaultFeatureConfig()).decorate(Decorator.HEIGHTMAP.configure(new HeightmapDecoratorConfig(Heightmap.Type.WORLD_SURFACE_WG))).repeat(100).spreadHorizontally();
    public static final ConfiguredFeature<?, ?> COBWEB_CONFIG = COBWEB_FEATURE.configure(new DefaultFeatureConfig()).decorate(Decorator.HEIGHTMAP.configure(new HeightmapDecoratorConfig(Heightmap.Type.WORLD_SURFACE_WG))).repeat(50).spreadHorizontally();
    public static final ConfiguredFeature<?, ?> BUSH_CONFIG = BUSH_FEATURE.configure(new DefaultFeatureConfig()).decorate(Decorator.HEIGHTMAP.configure(new HeightmapDecoratorConfig(Heightmap.Type.WORLD_SURFACE_WG))).repeat(35).spreadHorizontally();
    public static final ConfiguredFeature<?, ?> MOSSY_BOULDER_CONFIG = MOSSY_BOULDER_FEATURE.configure(new DefaultFeatureConfig()).decorate(Decorator.HEIGHTMAP.configure(new HeightmapDecoratorConfig(Heightmap.Type.WORLD_SURFACE_WG))).repeat(5).spreadHorizontally();
    public static final ConfiguredFeature<?, ?> GRAVESTONE_CONFIG = GRAVESTONE_FEATURE.configure(new DefaultFeatureConfig()).decorate(Decorator.HEIGHTMAP.configure(new HeightmapDecoratorConfig(Heightmap.Type.WORLD_SURFACE_WG))).spreadHorizontally();
    public static final ConfiguredFeature<?, ?> SOUL_LIGHT_CONFIG = SOUL_LIGHT_FEATURE.configure(new DefaultFeatureConfig()).decorate(Decorator.HEIGHTMAP.configure(new HeightmapDecoratorConfig(Heightmap.Type.WORLD_SURFACE_WG))).repeat(100).spreadHorizontally();


    // configured tree feature collections
    public static final ConfiguredFeature<?, ?> HAUNTED_FOREST_TREES = register("haunted_forest_trees", Feature.RANDOM_SELECTOR.configure(
            new RandomFeatureConfig(
                    ImmutableList.of(
                            TGConfiguredFeatures.SMALL_SPRUCE_TREE_01.configure(new TGTreeFeatureConfig(Blocks.STRIPPED_SPRUCE_LOG.getDefaultState(), Blocks.SPRUCE_LEAVES.getDefaultState())).withChance(0.25F),
                            TGConfiguredFeatures.SMALL_SPRUCE_TREE_02.configure(new TGTreeFeatureConfig(Blocks.STRIPPED_SPRUCE_LOG.getDefaultState(), Blocks.SPRUCE_LEAVES.getDefaultState())).withChance(0.25F),
                            TGConfiguredFeatures.SMALL_SPRUCE_TREE_03.configure(new TGTreeFeatureConfig(Blocks.STRIPPED_SPRUCE_LOG.getDefaultState(), Blocks.SPRUCE_LEAVES.getDefaultState())).withChance(0.375F),
                            TGConfiguredFeatures.SMALL_SPRUCE_TREE_04.configure(new TGTreeFeatureConfig(Blocks.STRIPPED_SPRUCE_LOG.getDefaultState(), Blocks.SPRUCE_LEAVES.getDefaultState())).withChance(0.1F),
                            TGConfiguredFeatures.SMALL_SPRUCE_TREE_05.configure(new TGTreeFeatureConfig(Blocks.STRIPPED_SPRUCE_LOG.getDefaultState(), Blocks.SPRUCE_LEAVES.getDefaultState())).withChance(0.1F),
                            TGConfiguredFeatures.SMALL_SPRUCE_TREE_06.configure(new TGTreeFeatureConfig(Blocks.STRIPPED_SPRUCE_LOG.getDefaultState(), Blocks.SPRUCE_LEAVES.getDefaultState())).withChance(0.1F),
                            TGConfiguredFeatures.SMALL_BENT_SPRUCE_TREE_01.configure(new TGTreeFeatureConfig(Blocks.STRIPPED_SPRUCE_LOG.getDefaultState(), Blocks.SPRUCE_LEAVES.getDefaultState())).withChance(0.1F),
                            TGConfiguredFeatures.FALLEN_SPRUCE_TREE.configure(new TGTreeFeatureConfig(Blocks.STRIPPED_SPRUCE_LOG.getDefaultState(), Blocks.SPRUCE_LEAVES.getDefaultState())).withChance(0.1F),
                            TGConfiguredFeatures.LARGE_SPRUCE_TREE_01.configure(new TGTreeFeatureConfig(Blocks.STRIPPED_SPRUCE_LOG.getDefaultState(), Blocks.SPRUCE_LEAVES.getDefaultState())).withChance(0.25F),
                            TGConfiguredFeatures.LARGE_SPRUCE_TREE_02.configure(new TGTreeFeatureConfig(Blocks.STRIPPED_SPRUCE_LOG.getDefaultState(), Blocks.SPRUCE_LEAVES.getDefaultState())).withChance(0.1F),
                            TGConfiguredFeatures.LARGE_SPRUCE_TREE_03.configure(new TGTreeFeatureConfig(Blocks.STRIPPED_SPRUCE_LOG.getDefaultState(), Blocks.SPRUCE_LEAVES.getDefaultState())).withChance(0.1F),
                            TGConfiguredFeatures.SMALL_SPRUCE_TREE_01.configure(new TGTreeFeatureConfig(Blocks.SPRUCE_LOG.getDefaultState(), Blocks.SPRUCE_LEAVES.getDefaultState())).withChance(0.25F),
                            TGConfiguredFeatures.SMALL_SPRUCE_TREE_02.configure(new TGTreeFeatureConfig(Blocks.SPRUCE_LOG.getDefaultState(), Blocks.SPRUCE_LEAVES.getDefaultState())).withChance(0.25F),
                            TGConfiguredFeatures.SMALL_SPRUCE_TREE_03.configure(new TGTreeFeatureConfig(Blocks.SPRUCE_LOG.getDefaultState(), Blocks.SPRUCE_LEAVES.getDefaultState())).withChance(0.375F),
                            TGConfiguredFeatures.SMALL_SPRUCE_TREE_04.configure(new TGTreeFeatureConfig(Blocks.SPRUCE_LOG.getDefaultState(), Blocks.SPRUCE_LEAVES.getDefaultState())).withChance(0.1F),
                            TGConfiguredFeatures.SMALL_SPRUCE_TREE_05.configure(new TGTreeFeatureConfig(Blocks.SPRUCE_LOG.getDefaultState(), Blocks.SPRUCE_LEAVES.getDefaultState())).withChance(0.1F),
                            TGConfiguredFeatures.SMALL_SPRUCE_TREE_06.configure(new TGTreeFeatureConfig(Blocks.SPRUCE_LOG.getDefaultState(), Blocks.SPRUCE_LEAVES.getDefaultState())).withChance(0.1F),
                            TGConfiguredFeatures.SMALL_BENT_SPRUCE_TREE_01.configure(new TGTreeFeatureConfig(Blocks.SPRUCE_LOG.getDefaultState(), Blocks.SPRUCE_LEAVES.getDefaultState())).withChance(0.1F),
                            TGConfiguredFeatures.FALLEN_SPRUCE_TREE.configure(new TGTreeFeatureConfig(Blocks.SPRUCE_LOG.getDefaultState(), Blocks.SPRUCE_LEAVES.getDefaultState())).withChance(0.1F),
                            TGConfiguredFeatures.LARGE_SPRUCE_TREE_01.configure(new TGTreeFeatureConfig(Blocks.SPRUCE_LOG.getDefaultState(), Blocks.SPRUCE_LEAVES.getDefaultState())).withChance(0.25F),
                            TGConfiguredFeatures.LARGE_SPRUCE_TREE_02.configure(new TGTreeFeatureConfig(Blocks.SPRUCE_LOG.getDefaultState(), Blocks.SPRUCE_LEAVES.getDefaultState())).withChance(0.1F),
                            TGConfiguredFeatures.LARGE_SPRUCE_TREE_03.configure(new TGTreeFeatureConfig(Blocks.SPRUCE_LOG.getDefaultState(), Blocks.SPRUCE_LEAVES.getDefaultState())).withChance(0.1F)
                            ),
                    ConfiguredFeatures.SPRUCE))
            .decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
            .decorate(Decorator.COUNT.configure(new CountConfig(17))));

    public static final ConfiguredFeature<?, ?> ERODED_HAUNTED_FOREST_TREES = register("eroded_haunted_forest_trees", Feature.RANDOM_SELECTOR.configure(
            new RandomFeatureConfig(
                    ImmutableList.of(
                            TGConfiguredFeatures.SMALL_BENT_SPRUCE_TREE_01.configure(new TGTreeFeatureConfig(Blocks.STRIPPED_SPRUCE_LOG.getDefaultState(), Blocks.SPRUCE_LEAVES.getDefaultState())).withChance(0.1F),
                            TGConfiguredFeatures.FALLEN_SPRUCE_TREE.configure(new TGTreeFeatureConfig(Blocks.STRIPPED_SPRUCE_LOG.getDefaultState(), Blocks.SPRUCE_LEAVES.getDefaultState())).withChance(0.75F),
                            TGConfiguredFeatures.LARGE_BENT_SPRUCE_TREE_01.configure(new TGTreeFeatureConfig(Blocks.STRIPPED_SPRUCE_LOG.getDefaultState(), Blocks.SPRUCE_LEAVES.getDefaultState())).withChance(0.1F),
                            TGConfiguredFeatures.LARGE_BENT_SPRUCE_TREE_02.configure(new TGTreeFeatureConfig(Blocks.STRIPPED_SPRUCE_LOG.getDefaultState(), Blocks.SPRUCE_LEAVES.getDefaultState())).withChance(0.1F),
                            TGConfiguredFeatures.LARGE_SPRUCE_TREE_01.configure(new TGTreeFeatureConfig(Blocks.STRIPPED_SPRUCE_LOG.getDefaultState(), Blocks.SPRUCE_LEAVES.getDefaultState())).withChance(0.5F),
                            TGConfiguredFeatures.LARGE_SPRUCE_TREE_02.configure(new TGTreeFeatureConfig(Blocks.STRIPPED_SPRUCE_LOG.getDefaultState(), Blocks.SPRUCE_LEAVES.getDefaultState())).withChance(0.1F),
                            TGConfiguredFeatures.LARGE_SPRUCE_TREE_03.configure(new TGTreeFeatureConfig(Blocks.STRIPPED_SPRUCE_LOG.getDefaultState(), Blocks.SPRUCE_LEAVES.getDefaultState())).withChance(0.1F),

                            TGConfiguredFeatures.SMALL_BENT_SPRUCE_TREE_01.configure(new TGTreeFeatureConfig(Blocks.SPRUCE_LOG.getDefaultState(), Blocks.SPRUCE_LEAVES.getDefaultState())).withChance(0.1F),
                            TGConfiguredFeatures.FALLEN_SPRUCE_TREE.configure(new TGTreeFeatureConfig(Blocks.SPRUCE_LOG.getDefaultState(), Blocks.SPRUCE_LEAVES.getDefaultState())).withChance(0.75F),
                            TGConfiguredFeatures.LARGE_BENT_SPRUCE_TREE_01.configure(new TGTreeFeatureConfig(Blocks.SPRUCE_LOG.getDefaultState(), Blocks.SPRUCE_LEAVES.getDefaultState())).withChance(0.1F),
                            TGConfiguredFeatures.LARGE_BENT_SPRUCE_TREE_02.configure(new TGTreeFeatureConfig(Blocks.SPRUCE_LOG.getDefaultState(), Blocks.SPRUCE_LEAVES.getDefaultState())).withChance(0.1F),
                            TGConfiguredFeatures.LARGE_SPRUCE_TREE_01.configure(new TGTreeFeatureConfig(Blocks.SPRUCE_LOG.getDefaultState(), Blocks.SPRUCE_LEAVES.getDefaultState())).withChance(0.5F),
                            TGConfiguredFeatures.LARGE_SPRUCE_TREE_02.configure(new TGTreeFeatureConfig(Blocks.SPRUCE_LOG.getDefaultState(), Blocks.SPRUCE_LEAVES.getDefaultState())).withChance(0.1F),
                            TGConfiguredFeatures.LARGE_SPRUCE_TREE_03.configure(new TGTreeFeatureConfig(Blocks.SPRUCE_LOG.getDefaultState(), Blocks.SPRUCE_LEAVES.getDefaultState())).withChance(0.1F)
                    ),
                    ConfiguredFeatures.SPRUCE))
            .decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
            .decorate(Decorator.COUNT.configure(new CountConfig(5))));





    private static <FC extends FeatureConfig> ConfiguredFeature<FC, ?> register(String id, ConfiguredFeature<FC, ?> feature) {
        return Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier(TheGraveyard.MOD_ID, id), feature);
    }

    public static void registerFeatures() {
        Registry.register(Registry.FEATURE, new Identifier(TheGraveyard.MOD_ID, "moss_carpet_feature"), MOSS_CARPET_FEATURE);
        Registry.register(Registry.FEATURE, new Identifier(TheGraveyard.MOD_ID, "cobweb_feature"), COBWEB_FEATURE);
        Registry.register(Registry.FEATURE, new Identifier(TheGraveyard.MOD_ID, "bush_feature"), BUSH_FEATURE);
        Registry.register(Registry.FEATURE, new Identifier(TheGraveyard.MOD_ID, "mossy_boulder_feature"), MOSSY_BOULDER_FEATURE);
        Registry.register(Registry.FEATURE, new Identifier(TheGraveyard.MOD_ID, "gravestone_feature"), GRAVESTONE_FEATURE);
        Registry.register(Registry.FEATURE, new Identifier(TheGraveyard.MOD_ID, "soul_light_feature"), SOUL_LIGHT_FEATURE);

        Registry.register(Registry.FEATURE, new Identifier(TheGraveyard.MOD_ID, "small_spruce_tree_01"), SMALL_SPRUCE_TREE_01);
        Registry.register(Registry.FEATURE, new Identifier(TheGraveyard.MOD_ID, "small_spruce_tree_02"), SMALL_SPRUCE_TREE_02);
        Registry.register(Registry.FEATURE, new Identifier(TheGraveyard.MOD_ID, "small_spruce_tree_03"), SMALL_SPRUCE_TREE_03);
        Registry.register(Registry.FEATURE, new Identifier(TheGraveyard.MOD_ID, "small_spruce_tree_04"), SMALL_SPRUCE_TREE_04);
        Registry.register(Registry.FEATURE, new Identifier(TheGraveyard.MOD_ID, "small_spruce_tree_05"), SMALL_SPRUCE_TREE_05);
        Registry.register(Registry.FEATURE, new Identifier(TheGraveyard.MOD_ID, "small_spruce_tree_06"), SMALL_SPRUCE_TREE_06);
        Registry.register(Registry.FEATURE, new Identifier(TheGraveyard.MOD_ID, "small_bent_spruce_tree_01"), SMALL_BENT_SPRUCE_TREE_01);
        Registry.register(Registry.FEATURE, new Identifier(TheGraveyard.MOD_ID, "fallen_spruce_tree"), FALLEN_SPRUCE_TREE);
        Registry.register(Registry.FEATURE, new Identifier(TheGraveyard.MOD_ID, "large_bent_spruce_tree_01"), LARGE_BENT_SPRUCE_TREE_01);
        Registry.register(Registry.FEATURE, new Identifier(TheGraveyard.MOD_ID, "large_bent_spruce_tree_02"), LARGE_BENT_SPRUCE_TREE_02);
        Registry.register(Registry.FEATURE, new Identifier(TheGraveyard.MOD_ID, "large_spruce_tree_01"), LARGE_SPRUCE_TREE_01);
        Registry.register(Registry.FEATURE, new Identifier(TheGraveyard.MOD_ID, "large_spruce_tree_02"), LARGE_SPRUCE_TREE_02);
        Registry.register(Registry.FEATURE, new Identifier(TheGraveyard.MOD_ID, "large_spruce_tree_03"), LARGE_SPRUCE_TREE_03);

    }

    public static void registerConfiguredFeatures() {
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, MOSS_CARPET_FEATURE_KEY.getValue(), MOSS_CARPET_CONFIG);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, COBWEB_FEATURE_KEY.getValue(), COBWEB_CONFIG);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, BUSH_FEATURE_KEY.getValue(), BUSH_CONFIG);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, MOSSY_BOULDER_FEATURE_KEY.getValue(), MOSSY_BOULDER_CONFIG);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, GRAVESTONE_FEATURE_KEY.getValue(), GRAVESTONE_CONFIG);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, SOUL_LIGHT_FEATURE_KEY.getValue(), SOUL_LIGHT_CONFIG);
    }
}
