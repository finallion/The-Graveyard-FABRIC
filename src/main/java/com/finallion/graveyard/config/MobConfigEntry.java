package com.finallion.graveyard.config;

import java.util.List;

public class MobConfigEntry {
    public final boolean enabled;
    public final int weight;
    public final int minGroup;
    public final int maxGroup;
    public final List<String> allowedBiomes;

    public MobConfigEntry(int weight, int minGroup, int maxGroup, List<String> allowedBiomes) {
        this(true, weight, minGroup, maxGroup, allowedBiomes);
    }

    private MobConfigEntry(boolean enabled, int weight, int minGroup, int maxGroup, List<String> allowedBiomes) {
        this.enabled = enabled;
        this.weight = weight;
        this.minGroup = minGroup;
        this.maxGroup = maxGroup;
        this.allowedBiomes = allowedBiomes;
    }

    public static MobConfigEntry of(int weight, int minGroup, int maxGroup, List<String> allowedBiomes) {
        return new MobConfigEntry(weight, minGroup, maxGroup, allowedBiomes);
    }
}
