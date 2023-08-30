package com.lion.graveyard.config;

public class ParticleConfigEntry {
    public final boolean canGenerate;
    public final int spawnChance;

    private ParticleConfigEntry(int spawnChance) {
        this(true, spawnChance);
    }

    private ParticleConfigEntry(boolean canGenerate, int spawnChance) {
        this.canGenerate = canGenerate;
        this.spawnChance = spawnChance;
    }

    public static ParticleConfigEntry of(int spawnChance) {
        return new ParticleConfigEntry(spawnChance);
    }
}
