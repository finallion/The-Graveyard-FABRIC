package com.finallion.graveyard.world.features.surfaceFeatures;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

public class FeatureUtil {

    public static boolean isCorrectBiome(RegistryKey<Biome> key) {
        if (key.getValue().getPath().contains("haunted")) {
            return true;
        }
        return false;
    }
}
