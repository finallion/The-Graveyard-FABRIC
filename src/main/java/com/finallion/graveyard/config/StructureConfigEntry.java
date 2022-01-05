package com.finallion.graveyard.config;

import java.util.Arrays;
import java.util.Collections;
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

    public StructureConfigEntry(int spacing, int separation, int salt) {
        this(true, spacing, separation, salt);
    }

    private StructureConfigEntry(boolean enabled, int spacing, int separation, int salt) {
        this.enabled = enabled;
        this.spacing = spacing;
        this.separation = separation;
        this.salt = salt;
    }

    public static StructureConfigEntry of(int spacing, int separation, int salt) {
        return new StructureConfigEntry(spacing, separation, salt);
    }

}
