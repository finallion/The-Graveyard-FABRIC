package com.lion.graveyard.config.entries;

public class MobConfigEntry {
    public final boolean enabled;
    public final int weight;
    public final int minGroup;
    public final int maxGroup;
    public final boolean canBurnInSunlight;
    public final boolean canBeWithered;

    private MobConfigEntry(boolean enabled, int weight, int minGroup, int maxGroup, boolean canBurnInSunlight, boolean canBeWithered) {
        this.enabled = enabled;
        this.weight = weight;
        this.minGroup = minGroup;
        this.maxGroup = maxGroup;
        this.canBurnInSunlight = canBurnInSunlight;
        this.canBeWithered = canBeWithered;
    }

    private MobConfigEntry(boolean enabled, boolean canBurnInSunlight, boolean canBeWithered) {
        this(enabled, 0, 0, 0, canBurnInSunlight, canBeWithered);
    }


    public static MobConfigEntry of(boolean enabled, boolean canBurnInSunlight, boolean canBeWithered) {
        return new MobConfigEntry(enabled, canBurnInSunlight, canBeWithered);
    }
    public static MobConfigEntry of(boolean enabled, int weight, int minGroup, int maxGroup, boolean canBurnInSunlight, boolean canBeWithered) {
        return new MobConfigEntry(enabled, weight, minGroup, maxGroup, canBurnInSunlight, canBeWithered);
    }
}
