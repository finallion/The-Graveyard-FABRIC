package com.finallion.graveyard.config;

public class ConfigBiomeFogParticleEntry {
    public final boolean canGenerate;
    public final float density;

    private ConfigBiomeFogParticleEntry(float density) {
        this(true, density);
    }

    private ConfigBiomeFogParticleEntry(boolean canGenerate, float density) {
        this.canGenerate = canGenerate;
        this.density = density;
    }

    public static ConfigBiomeFogParticleEntry of(float density) {
        return new ConfigBiomeFogParticleEntry(density);
    }
}
