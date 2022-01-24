package com.finallion.graveyard.config;

public class BiomeFogConfigEntry {
    public final boolean enabled;
    public final float density;
    public final int minY;
    public final int maxY;

    private BiomeFogConfigEntry(float density, int minY, int maxY) {
        this(true, density, minY, maxY);
    }

    private BiomeFogConfigEntry(boolean enabled, float density, int minY, int maxY) {
        this.enabled = enabled;
        this.density = density;
        this.minY = minY;
        this.maxY = maxY;
    }

    public static BiomeFogConfigEntry of(float density, int minY, int maxY) {
        return new BiomeFogConfigEntry(density, minY, maxY);
    }
}
