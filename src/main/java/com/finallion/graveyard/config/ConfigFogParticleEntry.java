package com.finallion.graveyard.config;

public class ConfigFogParticleEntry {
    public final boolean canGenerate;
    public final int spawnChance;

    private ConfigFogParticleEntry(int spawnChance) {
        this(true, spawnChance);
    }

    private ConfigFogParticleEntry(boolean canGenerate, int spawnChance) {
        this.canGenerate = canGenerate;
        this.spawnChance = spawnChance;
    }

    public static ConfigFogParticleEntry of(int spawnChance) {
        return new ConfigFogParticleEntry(spawnChance);
    }
}
