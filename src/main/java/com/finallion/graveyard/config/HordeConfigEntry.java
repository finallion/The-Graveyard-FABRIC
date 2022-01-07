package com.finallion.graveyard.config;

public class HordeConfigEntry {
    public final boolean enabled;
    public final int size;
    public final int ticksUntilNextSpawn;

    public HordeConfigEntry(int size, int ticksUntilNextSpawn) {
        this(true, size, ticksUntilNextSpawn);
    }

    private HordeConfigEntry(boolean enabled, int size, int ticksUntilNextSpawn) {
        this.enabled = enabled;
        this.size = size;
        this.ticksUntilNextSpawn = ticksUntilNextSpawn;
    }

    public static HordeConfigEntry of(int size, int ticksUntilNextSpawn) {
        return new HordeConfigEntry(size, ticksUntilNextSpawn);
    }
}
