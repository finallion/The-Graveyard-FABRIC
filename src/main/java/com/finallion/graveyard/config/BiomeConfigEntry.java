package com.finallion.graveyard.config;

public class BiomeConfigEntry {
    public final boolean canGenerate;

    private BiomeConfigEntry() {
        this(true);
    }

    private BiomeConfigEntry(boolean canGenerate) {
        this.canGenerate = canGenerate;
    }

    public static BiomeConfigEntry of() {
        return new BiomeConfigEntry();
    }
}
