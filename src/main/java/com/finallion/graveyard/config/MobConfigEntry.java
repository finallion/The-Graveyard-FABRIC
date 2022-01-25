package com.finallion.graveyard.config;

import java.util.List;

public class MobConfigEntry {
    public final boolean enabled;
    public final int weight;
    public final int minGroup;
    public final int maxGroup;
    public final List<String> allowedBiomeCategories;
    public final List<String> blacklistedBiomes;

    public MobConfigEntry(int weight, int minGroup, int maxGroup, List<String> allowedBiomeCategories, List<String> blacklistedBiomes) {
        this(true, weight, minGroup, maxGroup, allowedBiomeCategories, blacklistedBiomes);
    }

    private MobConfigEntry(boolean enabled, int weight, int minGroup, int maxGroup, List<String> allowedBiomeCategories, List<String> blacklistedBiomes) {
        this.enabled = enabled;
        this.weight = weight;
        this.minGroup = minGroup;
        this.maxGroup = maxGroup;
        this.allowedBiomeCategories = allowedBiomeCategories;
        this.blacklistedBiomes = blacklistedBiomes;
    }

    public static MobConfigEntry of(int weight, int minGroup, int maxGroup, List<String> allowedBiomeCategories, List<String> blacklistedBiomes) {
        return new MobConfigEntry(weight, minGroup, maxGroup, allowedBiomeCategories, blacklistedBiomes);
    }
}
