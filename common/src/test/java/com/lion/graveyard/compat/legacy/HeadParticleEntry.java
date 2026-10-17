package com.lion.graveyard.compat.legacy;

public class HeadParticleEntry {
    public final boolean canGenerate;
    public final int spawnChance;

    private HeadParticleEntry(int spawnChance) {
        this(true, spawnChance);
    }

    private HeadParticleEntry(boolean canGenerate, int spawnChance) {
        this.canGenerate = canGenerate;
        this.spawnChance = spawnChance;
    }

    public HeadParticleEntry sanitized() {
        return new HeadParticleEntry(canGenerate, Math.max(1, Math.min(spawnChance, 100_000)));
    }

    public static HeadParticleEntry of(int spawnChance) {
        return new HeadParticleEntry(spawnChance);
    }
}
