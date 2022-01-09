package com.finallion.graveyard.config;

import java.util.List;

public class MobConfigEntry {
    public final boolean enabled;
    public final int weight;
    public final int minGroup;
    public final int maxGroup;
    public final List<String> allowedBiomesAndBiomeCategories;

    public MobConfigEntry(int weight, int minGroup, int maxGroup, List<String> allowedBiomesAndBiomeCategories) {
        this(true, weight, minGroup, maxGroup, allowedBiomesAndBiomeCategories);
    }

    private MobConfigEntry(boolean enabled, int weight, int minGroup, int maxGroup, List<String> allowedBiomesAndBiomeCategories) {
        this.enabled = enabled;
        this.weight = weight;
        this.minGroup = minGroup;
        this.maxGroup = maxGroup;
        this.allowedBiomesAndBiomeCategories = allowedBiomesAndBiomeCategories;
    }

    public static MobConfigEntry of(int weight, int minGroup, int maxGroup, List<String> allowedBiomesAndBiomeCategories) {
        return new MobConfigEntry(weight, minGroup, maxGroup, allowedBiomesAndBiomeCategories);
    }
}
