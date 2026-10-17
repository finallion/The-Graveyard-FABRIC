package com.lion.graveyard.config.entries;

public class StructureConfigEntry {
    public final boolean enabled;
    public final Double chance;
    public final Integer minY;
    public final Integer maxY;

    private StructureConfigEntry(boolean enabled, Double chance, Integer minY, Integer maxY) {
        this.enabled = enabled;
        this.chance = chance;
        this.minY = minY;
        this.maxY = maxY;
    }

    public double spawnChance() {
        return chance == null ? 1.0D : Math.max(0.0D, Math.min(1.0D, chance));
    }

    public int lowestY(int fallback) {
        return minY == null ? fallback : minY;
    }

    public int highestY(int fallback) {
        return maxY == null ? fallback : maxY;
    }

    public static StructureConfigEntry of(boolean enabled, double chance, Integer minY, Integer maxY) {
        return new StructureConfigEntry(enabled, chance, minY, maxY);
    }

    public static StructureConfigEntry of() {
        return new StructureConfigEntry(true, 1.0D, null, null);
    }
}
