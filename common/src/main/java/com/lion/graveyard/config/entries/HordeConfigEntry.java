package com.lion.graveyard.config.entries;

public class HordeConfigEntry {
    public final boolean enabled;
    public final int mobSpawnAttempts;
    public final int ticksUntilNextSpawn;
    public final int additionalRandomizedTicks;

    public HordeConfigEntry(int mobSpawnAttempts, int ticksUntilNextSpawn, int additionalRandomizedTicks) {
        this(true, mobSpawnAttempts, ticksUntilNextSpawn, additionalRandomizedTicks);
    }

    private HordeConfigEntry(boolean enabled, int mobSpawnAttempts, int ticksUntilNextSpawn, int additionalRandomizedTicks) {
        this.enabled = enabled;
        this.mobSpawnAttempts = mobSpawnAttempts;
        this.ticksUntilNextSpawn = ticksUntilNextSpawn;
        this.additionalRandomizedTicks = additionalRandomizedTicks;
    }

    public HordeConfigEntry sanitized() {
        return new HordeConfigEntry(enabled, Math.max(0, Math.min(mobSpawnAttempts, 256)),
                Math.max(0, ticksUntilNextSpawn), Math.max(0, additionalRandomizedTicks));
    }

    public static HordeConfigEntry of(int mobSpawnAttempts, int ticksUntilNextSpawn, int additionalRandomizedTicks) {
        return new HordeConfigEntry(mobSpawnAttempts, ticksUntilNextSpawn, additionalRandomizedTicks);
    }
}
