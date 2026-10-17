package com.lion.graveyard.config.entries;

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

    public ParticleConfigEntry sanitized() {
        return new ParticleConfigEntry(canGenerate, Math.max(1, Math.min(spawnChance, 100_000)));
    }

    public static ParticleConfigEntry of(int spawnChance) {
        return new ParticleConfigEntry(spawnChance);
    }
}
