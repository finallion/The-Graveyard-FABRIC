package com.lion.graveyard.compat.legacy;

public class HeadHordeEntry {
    public final boolean enabled;
    public final int mobSpawnAttempts;
    public final int ticksUntilNextSpawn;
    public final int additionalRandomizedTicks;
    public final Double chancePerCheck;
    public final Integer minDistance;
    public final Integer maxDistance;
    public final Boolean nightOnly;

    public HeadHordeEntry(int mobSpawnAttempts, int ticksUntilNextSpawn, int additionalRandomizedTicks) {
        this(true, mobSpawnAttempts, ticksUntilNextSpawn, additionalRandomizedTicks, 0.2D, 24, 48, true);
    }

    private HeadHordeEntry(
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

    public HeadHordeEntry sanitized() {
        return new HeadHordeEntry(
                enabled,
                Math.max(0, Math.min(mobSpawnAttempts, 256)),
                Math.max(0, ticksUntilNextSpawn),
                Math.max(0, additionalRandomizedTicks),
                chance(),
                nearDistance(),
                farDistance(),
                onlyAtNight()
        );
    }

    public static HeadHordeEntry of(int mobSpawnAttempts, int ticksUntilNextSpawn, int additionalRandomizedTicks) {
        return new HeadHordeEntry(mobSpawnAttempts, ticksUntilNextSpawn, additionalRandomizedTicks);
    }
}
