package com.lion.graveyard.compat.legacy;

public class HeadStructureEntry {
    public final boolean enabled;
    public final Double chance;
    public final Integer minY;
    public final Integer maxY;

    private HeadStructureEntry(boolean enabled, Double chance, Integer minY, Integer maxY) {
        this.enabled = enabled;
        this.chance = chance;
        this.minY = minY;
        this.maxY = maxY;
    }

    public double spawnChance() {
        return chance == null ? 1.0D : Math.max(0.0D, Math.min(1.0D, chance));
    }

    public HeadStructureEntry sanitized() {
        return new HeadStructureEntry(enabled, spawnChance(), minY, maxY);
    }

    public HeadStructureEntry sanitized(int defaultMinY, int defaultMaxY) {
        return new HeadStructureEntry(enabled, spawnChance(), minY == null ? defaultMinY : minY, maxY == null ? defaultMaxY : maxY);
    }

    public int lowestY(int fallback) {
        return minY == null ? fallback : minY;
    }

    public int highestY(int fallback) {
        return maxY == null ? fallback : maxY;
    }

    public static HeadStructureEntry of() {
        return new HeadStructureEntry(true, 1.0D, null, null);
    }
}
