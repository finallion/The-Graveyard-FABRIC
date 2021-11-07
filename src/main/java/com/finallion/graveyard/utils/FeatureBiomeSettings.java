package com.finallion.graveyard.utils;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.init.TGBiomes;
import com.finallion.graveyard.init.TGConfiguredFeatures;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;


public class FeatureBiomeSettings {

    public static void init() {
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(TGBiomes.HAUNTED_FOREST_KEY), GenerationStep.Feature.VEGETAL_DECORATION, TGConfiguredFeatures.MOSS_CARPET_FEATURE_KEY);
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(TGBiomes.HAUNTED_FOREST_KEY), GenerationStep.Feature.UNDERGROUND_ORES, TGConfiguredFeatures.COBWEB_FEATURE_KEY);
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(TGBiomes.HAUNTED_FOREST_KEY), GenerationStep.Feature.VEGETAL_DECORATION, TGConfiguredFeatures.BUSH_FEATURE_KEY);

        BiomeModifications.addFeature(BiomeSelectors.includeByKey(TGBiomes.HAUNTED_FOREST_LAKE_KEY), GenerationStep.Feature.VEGETAL_DECORATION, TGConfiguredFeatures.MOSS_CARPET_FEATURE_KEY);
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(TGBiomes.HAUNTED_FOREST_LAKE_KEY), GenerationStep.Feature.UNDERGROUND_ORES, TGConfiguredFeatures.COBWEB_FEATURE_KEY);
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(TGBiomes.HAUNTED_FOREST_LAKE_KEY), GenerationStep.Feature.VEGETAL_DECORATION, TGConfiguredFeatures.BUSH_FEATURE_KEY);
        //BiomeModifications.addFeature(BiomeSelectors.includeByKey(TGBiomes.HAUNTED_FOREST_KEY), GenerationStep.Feature.VEGETAL_DECORATION, TGConfiguredFeatures.MOSSY_BOULDER_FEATURE_KEY);

    }
}
