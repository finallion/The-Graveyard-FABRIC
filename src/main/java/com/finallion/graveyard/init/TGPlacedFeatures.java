package com.finallion.graveyard.init;

import com.finallion.graveyard.TheGraveyard;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placementmodifier.*;

import java.util.ArrayList;
import java.util.List;

public class TGPlacedFeatures {
/*
    public static final RegistryKey<PlacedFeature> SCARLET_FOREST_BOULDERS = createRegistryKey("scarlet_forest_boulders");

    public static void populate(FabricDynamicRegistryProvider.Entries entries) {
        entries.add(SCARLET_FOREST_BOULDERS, placeFeature(entries, TGFeatures.BOULDER_CONFIGURED_FEATURE, CountPlacementModifier.of(2), SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of()));
    }

    public static RegistryKey<PlacedFeature> createRegistryKey(String name) {
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of(TheGraveyard.MOD_ID, name));
    }

    private static PlacedFeature placeFeature(FabricDynamicRegistryProvider.Entries entries, RegistryKey<ConfiguredFeature<?, ?>> feature, PlacementModifier... placementModifiers) {
        List<PlacementModifier> list = new ArrayList<>(List.of(placementModifiers));
        list.add(BiomePlacementModifier.of());
        return placeFeature(entries, feature, list);
    }

    private static PlacedFeature placeFeatureWithoutBiomeFilter(FabricDynamicRegistryProvider.Entries entries, RegistryKey<ConfiguredFeature<?, ?>> feature, PlacementModifier... placementModifiers) {
        List<PlacementModifier> list = new ArrayList<>(List.of(placementModifiers));
        return placeFeature(entries, feature, list);
    }

    private static PlacedFeature placeFeature(FabricDynamicRegistryProvider.Entries entries, RegistryKey<ConfiguredFeature<?, ?>> feature, List<PlacementModifier> list) {
        return new PlacedFeature(entries.ref(feature), list);
    }

    public static void init() {}

 */
}
