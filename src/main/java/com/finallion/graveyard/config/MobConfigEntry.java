package com.finallion.graveyard.config;

import java.util.List;

public class MobConfigEntry {
    public final boolean enabled;
    public final int weight;
    public final int minGroup;
    public final int maxGroup;
    public final boolean canBurnInSunlight;
    public final boolean canBeWithered;
    public final List<String> whitelistedBiomesAndCategories;
    public final List<String> blacklistedBiomes;


    private MobConfigEntry(boolean enabled, int weight, int minGroup, int maxGroup, boolean canBurnInSunlight, boolean canBeWithered, List<String> whitelistedBiomesAndCategories, List<String> blacklistedBiomes) {
        this.enabled = enabled;
        this.weight = weight;
        this.minGroup = minGroup;
        this.maxGroup = maxGroup;
        this.canBurnInSunlight = canBurnInSunlight;
        this.canBeWithered = canBeWithered;
        this.whitelistedBiomesAndCategories = whitelistedBiomesAndCategories;
        this.blacklistedBiomes = blacklistedBiomes;
    }

    public static MobConfigEntry of(boolean enabled, int weight, int minGroup, int maxGroup, boolean canBurnInSunlight, boolean canBeWithered, List<String> whitelistedBiomesAndCategories, List<String> blacklistedBiomes) {
        return new MobConfigEntry(enabled, weight, minGroup, maxGroup, canBurnInSunlight, canBeWithered, whitelistedBiomesAndCategories, blacklistedBiomes);
    }
}
