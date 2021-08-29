package com.finallion.graveyard.config;

public class ConfigStructureEntry {
    public final boolean enabled;
    public final int separation;
    public final int spacing;
    public final int salt;

    private ConfigStructureEntry(int spacing, int separation, int salt) {
        this(true, spacing, separation, salt);
    }

    private ConfigStructureEntry(boolean enabled, int spacing, int separation, int salt) {
        this.enabled = enabled;
        this.spacing = spacing;
        this.separation = separation;
        this.salt = salt;
    }

    public static ConfigStructureEntry of(int spacing, int separation, int salt) {
        return new ConfigStructureEntry(spacing, separation, salt);
    }
}
