package com.finallion.graveyard.config;

import java.util.List;

public class StructureConfigEntry {
    public final boolean enabled;
    public final int separation;
    public final int spacing;
    public final int salt;
    public final List<String> whitelist;
    public final List<String> blacklist;
    public final List<String> modWhitelist;
    public final boolean canSpawnGraveyardMobs;

    public StructureConfigEntry(int spacing, int separation, int salt, List<String> whitelist, List<String> blacklist, List<String> modWhitelist, boolean canSpawnGraveyardMobs) {
        this(true, spacing, separation, salt, whitelist, blacklist, modWhitelist, canSpawnGraveyardMobs);
    }

    private StructureConfigEntry(boolean enabled, int spacing, int separation, int salt, List<String> whitelist, List<String> blacklist, List<String> modWhitelist, boolean canSpawnGraveyardMobs) {
        this.enabled = enabled;
        this.spacing = spacing;
        this.separation = separation;
        this.salt = salt;
        this.whitelist = whitelist;
        this.blacklist = blacklist;
        this.modWhitelist = modWhitelist;
        this.canSpawnGraveyardMobs = canSpawnGraveyardMobs;
    }

    public static StructureConfigEntry of(int spacing, int separation, int salt, List<String> whitelist, List<String> blacklist, List<String> modWhitelist, boolean canSpawnGraveyardMobs) {
        return new StructureConfigEntry(spacing, separation, salt, whitelist, blacklist, modWhitelist, canSpawnGraveyardMobs);
    }

}
