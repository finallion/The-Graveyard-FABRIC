package com.lion.graveyard.config.entries;

public class HordeConfigEntry {
    public final boolean enabled;
    public final int mobSpawnAttempts;
    public final int ticksUntilNextSpawn;
    public final int additionalRandomizedTicks;
    public final Double chancePerCheck;
    public final Integer minDistance;
    public final Integer maxDistance;
    public final Boolean nightOnly;

    public HordeConfigEntry(int mobSpawnAttempts, int ticksUntilNextSpawn, int additionalRandomizedTicks) {
        this(true, mobSpawnAttempts, ticksUntilNextSpawn, additionalRandomizedTicks, 0.2D, 24, 48, true);
    }

    private HordeConfigEntry(
            boolean enabled,
            int mobSpawnAttempts,
            int ticksUntilNextSpawn,
            int additionalRandomizedTicks,
            Double chancePerCheck,
            Integer minDistance,
            Integer maxDistance,
            Boolean nightOnly
    ) {
        this.enabled = enabled;
        this.mobSpawnAttempts = mobSpawnAttempts;
        this.ticksUntilNextSpawn = ticksUntilNextSpawn;
        this.additionalRandomizedTicks = additionalRandomizedTicks;
        this.chancePerCheck = chancePerCheck;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.nightOnly = nightOnly;
    }

    public double chance() {
        return chancePerCheck == null ? 0.2D : Math.max(0.0D, Math.min(1.0D, chancePerCheck));
    }

    public int nearDistance() {
        return minDistance == null ? 24 : Math.max(1, minDistance);
    }

    public int farDistance() {
        return Math.max(nearDistance(), maxDistance == null ? 48 : maxDistance);
    }

    public boolean onlyAtNight() {
        return nightOnly == null || nightOnly;
    }

    public static HordeConfigEntry of(
            boolean enabled,
            int mobSpawnAttempts,
            int ticksUntilNextSpawn,
            int additionalRandomizedTicks,
            double chancePerCheck,
            int minDistance,
            int maxDistance,
            boolean nightOnly
    ) {
        return new HordeConfigEntry(enabled, mobSpawnAttempts, ticksUntilNextSpawn, additionalRandomizedTicks, chancePerCheck, minDistance, maxDistance, nightOnly);
    }

    public static HordeConfigEntry of(int mobSpawnAttempts, int ticksUntilNextSpawn, int additionalRandomizedTicks) {
        return new HordeConfigEntry(mobSpawnAttempts, ticksUntilNextSpawn, additionalRandomizedTicks);
    }
}
