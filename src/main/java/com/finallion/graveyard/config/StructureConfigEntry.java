package com.finallion.graveyard.config;

import java.util.List;

public class StructureConfigEntry {
    /*
    public final boolean canGenerate;
    private final int separation;
    private final int spacing;
    private final List<String> allowedBiomeCategories;
    private final List<String> blacklistedBiomes;

    public StructureConfigEntry(boolean canGenerate, int spacing, int separation, String... biomeCategories) {
        this.spacing = spacing;
        this.separation = separation;
        this.allowedBiomeCategories = Arrays.asList(biomeCategories);
        this.blacklistedBiomes = Collections.emptyList();
    }

    public StructureConfigEntry(int spacing, int separation, String... biomeCategories) {
        this.spacing = spacing;
        this.separation = separation;
        this.allowedBiomeCategories = Arrays.asList(biomeCategories);
        this.blacklistedBiomes = Collections.emptyList();
    }

    public StructureConfigEntry(int spacing, int separation, List<String> biomeCategories, List<String> blacklistedBiomes) {
        this.spacing = spacing;
        this.separation = separation;
        this.allowedBiomeCategories = biomeCategories;
        this.blacklistedBiomes = blacklistedBiomes;
    }

    public int getSpacing() {
        return spacing;
    }

    public int getSeparation() {
        return separation;
    }

    public List<String> getBiomeCategories() {
        return allowedBiomeCategories;
    }

    public List<String> getBlacklistedBiomes() {
        return blacklistedBiomes;
    }

     */

    public final boolean enabled;
    public final int separation;
    public final int spacing;
    public final int salt;
    public final List<String> allowedBiomeCategories;
    public final List<String> blacklistedBiomes;

    public StructureConfigEntry(int spacing, int separation, int salt, List<String> allowedBiomeCategories, List<String> blacklistedBiomes) {
        this(true, spacing, separation, salt, allowedBiomeCategories, blacklistedBiomes);
    }

    private StructureConfigEntry(boolean enabled, int spacing, int separation, int salt, List<String> allowedBiomeCategories, List<String> blacklistedBiomes) {
        this.enabled = enabled;
        this.spacing = spacing;
        this.separation = separation;
        this.salt = salt;
        this.allowedBiomeCategories = allowedBiomeCategories;
        this.blacklistedBiomes = blacklistedBiomes;
    }

    public static StructureConfigEntry of(int spacing, int separation, int salt, List<String> allowedBiomeCategory, List<String> blacklistedBiomes) {
        return new StructureConfigEntry(spacing, separation, salt, allowedBiomeCategory, blacklistedBiomes);
    }

}
