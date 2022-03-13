package com.finallion.graveyard.init;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.world.features.surfaceFeatures.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.CountConfig;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placementmodifier.*;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;

import java.util.List;

public class TGConfiguredFeatures {

    // features
    private static final Feature<DefaultFeatureConfig> GRAVESTONE_FEATURE = registerFeature("gravestone_feature", new GraveFeature(DefaultFeatureConfig.CODEC));

    // configured features
    public static final RegistryEntry<ConfiguredFeature<DefaultFeatureConfig, ?>> GRAVESTONE_CONFIG_FEATURE = registerConfiguredFeature("gravestone_feature", GRAVESTONE_FEATURE, FeatureConfig.DEFAULT);

    // placed features
    public static final RegistryEntry<PlacedFeature> GRAVESTONE_PLACED_FEATURE = registerPlacedFeature("gravestone_feature", GRAVESTONE_CONFIG_FEATURE, RarityFilterPlacementModifier.of(1), SquarePlacementModifier.of(), PlacedFeatures.WORLD_SURFACE_WG_HEIGHTMAP, CountPlacementModifier.of(1));

    public static void init() {

    }

    public static RegistryEntry<PlacedFeature> registerPlacedFeature(String id, RegistryEntry<? extends ConfiguredFeature<?, ?>> registryEntry, PlacementModifier... modifiers) {
        return BuiltinRegistries.add(BuiltinRegistries.PLACED_FEATURE, new Identifier(TheGraveyard.MOD_ID, id), new PlacedFeature(RegistryEntry.upcast(registryEntry), List.of(modifiers)));
    }

    public static <FC extends FeatureConfig, F extends Feature<FC>> RegistryEntry<ConfiguredFeature<FC, ?>> registerConfiguredFeature(String id, F feature, FC config) {
        return BuiltinRegistries.method_40360(BuiltinRegistries.CONFIGURED_FEATURE, id, new ConfiguredFeature(feature, config));
    }

    private static <C extends FeatureConfig, F extends Feature<C>> F registerFeature(String id, F feature) {
        return Registry.register(Registry.FEATURE, new Identifier(TheGraveyard.MOD_ID, id), feature);
    }


}
