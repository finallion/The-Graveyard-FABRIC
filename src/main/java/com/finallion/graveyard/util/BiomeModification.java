package com.finallion.graveyard.util;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.init.TGConfiguredFeatures;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.GenerationStep;

public class BiomeModification {
    public static final RegistryKey<Biome> ERODED_HAUNTED_FOREST_KEY = RegistryKey.of(Registry.BIOME_KEY, new Identifier("graveyard_biomes:eroded_haunted_forest"));
    public static final RegistryKey<Biome> HAUNTED_LAKES_KEY = RegistryKey.of(Registry.BIOME_KEY, new Identifier("graveyard_biomes:haunted_lakes"));
    public static final RegistryKey<Biome> HAUNTED_FOREST_KEY = RegistryKey.of(Registry.BIOME_KEY, new Identifier("graveyard_biomes:haunted_forest"));
    public static final RegistryKey<Biome> ANCIENT_DEAD_CORAL_REEF_KEY = RegistryKey.of(Registry.BIOME_KEY, new Identifier("graveyard_biomes:ancient_dead_coral_reef"));

    public static void init() {
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(ERODED_HAUNTED_FOREST_KEY), GenerationStep.Feature.VEGETAL_DECORATION, TGConfiguredFeatures.GRAVESTONE_PLACED_FEATURE.getKey().get());
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(HAUNTED_LAKES_KEY), GenerationStep.Feature.VEGETAL_DECORATION, TGConfiguredFeatures.GRAVESTONE_PLACED_FEATURE.getKey().get());
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(HAUNTED_FOREST_KEY), GenerationStep.Feature.VEGETAL_DECORATION, TGConfiguredFeatures.GRAVESTONE_PLACED_FEATURE.getKey().get());
    }


}
