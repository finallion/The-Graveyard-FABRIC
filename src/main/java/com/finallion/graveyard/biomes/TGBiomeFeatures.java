package com.finallion.graveyard.biomes;

import com.finallion.graveyard.init.TGConfiguredFeatures;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.gen.GenerationStep;

public class TGBiomeFeatures {

    public static void addGraveyardSpruceTrees(GenerationSettings.Builder gen) {
        gen.feature(GenerationStep.Feature.VEGETAL_DECORATION, TGConfiguredFeatures.HAUNTED_FOREST_TREES);
    }
}
