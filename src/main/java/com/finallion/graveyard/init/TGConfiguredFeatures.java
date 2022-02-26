package com.finallion.graveyard.init;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.world.features.surfaceFeatures.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.CountConfig;
import net.minecraft.world.gen.decorator.*;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;

import java.util.List;

public class TGConfiguredFeatures {

    private static final Feature<DefaultFeatureConfig> GRAVESTONE_FEATURE = new GraveFeature(DefaultFeatureConfig.CODEC);

    // configured features registry keys
    public static final RegistryKey<ConfiguredFeature<?, ?>> GRAVESTONE_FEATURE_KEY = RegistryKey.of(Registry.CONFIGURED_FEATURE_KEY, new Identifier(TheGraveyard.MOD_ID, "gravestone_feature"));


    // configured features
    public static final ConfiguredFeature<?, ?> GRAVESTONE_CONFIG = GRAVESTONE_FEATURE.configure(new DefaultFeatureConfig());

    // placed features
    public static PlacedFeature GRAVESTONE_PLACED_FEATURE = GRAVESTONE_CONFIG.withPlacement(RarityFilterPlacementModifier.of(1), SquarePlacementModifier.of(), PlacedFeatures.WORLD_SURFACE_WG_HEIGHTMAP, CountPlacementModifier.of(1));


    public static void registerFeatures() {
        Registry.register(Registry.FEATURE, new Identifier(TheGraveyard.MOD_ID, "gravestone_feature"), GRAVESTONE_FEATURE);
    }

    public static void registerConfiguredFeatures() {
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, GRAVESTONE_FEATURE_KEY.getValue(), GRAVESTONE_CONFIG);

        Registry.register(BuiltinRegistries.PLACED_FEATURE, new Identifier(TheGraveyard.MOD_ID, "gravestone_placed_feature"), GRAVESTONE_PLACED_FEATURE);

    }
}
