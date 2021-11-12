package com.finallion.graveyard.config;

public class ConfigBiomeEntry {
    public final boolean biomeSpawn;
    public final boolean biomeFogSpawn;
    public final float biomeChance;
    public final float fogDensity;
    public final int fogMaxY;
    public final int fogMinY;

    private ConfigBiomeEntry(boolean biomeFogSpawn, float fogDensity, float biomeChance, int fogMaxY, int fogMinY) {
        this(true, biomeFogSpawn, fogDensity, biomeChance, fogMaxY, fogMinY);
    }

    private ConfigBiomeEntry(boolean biomeSpawn, boolean biomeFogSpawn, float fogDensity, float biomeChance, int fogMaxY, int fogMinY) {
        this.biomeSpawn = biomeSpawn;
        this.biomeFogSpawn = biomeFogSpawn;
        this.fogDensity = fogDensity;
        this.biomeChance = biomeChance;
        this.fogMaxY = fogMaxY;
        this.fogMinY = fogMinY;
    }

    public static ConfigBiomeEntry of(boolean biomeFogSpawn, float fogDensity, float biomeChance, int fogMaxY, int fogMinY) {
        return new ConfigBiomeEntry(biomeFogSpawn, fogDensity, biomeChance, fogMaxY, fogMinY);
    }
}
