package com.finallion.graveyard.config;

public class ConfigBiomeEntry {
    public final boolean biomeSpawn;
    public final boolean biomeFogSpawn;
    public final float biomeChance;
    public final float fogDensity;

    private ConfigBiomeEntry(boolean biomeFogSpawn, float fogDensity, float biomeChance) {
        this(true, biomeFogSpawn, fogDensity, biomeChance);
    }

    private ConfigBiomeEntry(boolean biomeSpawn, boolean biomeFogSpawn, float fogDensity, float biomeChance) {
        this.biomeSpawn = biomeSpawn;
        this.biomeFogSpawn = biomeFogSpawn;
        this.fogDensity = fogDensity;
        this.biomeChance = biomeChance;
    }

    public static ConfigBiomeEntry of(boolean biomeFogSpawn, float fogDensity, float biomeChance) {
        return new ConfigBiomeEntry(biomeFogSpawn, fogDensity, biomeChance);
    }
}
