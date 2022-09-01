package com.finallion.graveyard.config;

import java.util.List;

public class StructureConfigEntry {
    public final boolean enabled;
    public final int separation;
    public final int spacing;
    public final int salt;
    public final List<String> biomeWhitelist;
    public final List<String> biomeBlacklist;
    public final int terrainCheckRadius;
    public final int maxTerrainHeightDifference;
    public final boolean canSpawnGraveyardMobs;

    public StructureConfigEntry(int spacing, int separation, int salt, List<String> biomeWhitelist, List<String> biomeBlacklist, int terrainCheckRadius, int maxTerrainHeightDifference, boolean canSpawnGraveyardMobs) {
        this(true, spacing, separation, salt, biomeWhitelist, biomeBlacklist, terrainCheckRadius, maxTerrainHeightDifference, canSpawnGraveyardMobs);
    }

    private StructureConfigEntry(boolean enabled, int spacing, int separation, int salt, List<String> biomeWhitelist, List<String> biomeBlacklist, int terrainCheckRadius, int maxTerrainHeightDifference, boolean canSpawnGraveyardMobs) {
        this.enabled = enabled;
        this.spacing = spacing;
        this.separation = separation;
        this.salt = salt;
        this.biomeWhitelist = biomeWhitelist;
        this.biomeBlacklist = biomeBlacklist;
        this.terrainCheckRadius = terrainCheckRadius;
        this.maxTerrainHeightDifference = maxTerrainHeightDifference;
        this.canSpawnGraveyardMobs = canSpawnGraveyardMobs;
    }

    public static StructureConfigEntry of(int spacing, int separation, int salt, List<String> whitelist, List<String> biomeBlacklist, int terrainCheckRadius, int maxTerrainHeightDifference, boolean canSpawnGraveyardMobs) {
        return new StructureConfigEntry(spacing, separation, salt, whitelist, biomeBlacklist, terrainCheckRadius, maxTerrainHeightDifference, canSpawnGraveyardMobs);
    }

}
