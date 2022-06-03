package com.finallion.graveyard.config;

import java.util.List;

public class StructureConfigEntry {
    public final boolean enabled;
    public final int separation;
    public final int spacing;
    public final int salt;
    public final List<String> biomeWhitelist;
    public final List<String> modIdWhitelist;
    public final int terrainCheckRadius;
    public final int maxTerrainHeightDifference;
    public final boolean canSpawnGraveyardMobs;

    /*
    public static final Codec<StructureConfigEntry> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                            Codec.BOOL.fieldOf("use_expansion_hack").forGetter(config -> config.enabled),
                            Codec.INT.fieldOf("max_distance_from_center").forGetter(structure -> structure.spacing),
                            Codec.INT.fieldOf("max_distance_from_center").forGetter(structure -> structure.separation),
                            Codec.INT.fieldOf("max_distance_from_center").forGetter(structure -> structure.salt),
                            Codec.STRING.listOf().fieldOf("whitelist").orElse(new ArrayList<>()).forGetter(config -> config.whitelist),
                            Codec.STRING.listOf().fieldOf("blacklist").orElse(new ArrayList<>()).forGetter(config -> config.blacklist),
                            Codec.STRING.listOf().fieldOf("mod_whitelist").orElse(new ArrayList<>()).forGetter(config -> config.modWhitelist),
                            Codec.BOOL.fieldOf("use_expansion_hack").forGetter(config -> config.canSpawnGraveyardMobs))
                    .apply(instance, StructureConfigEntry::new));



     */
    public StructureConfigEntry(int spacing, int separation, int salt, List<String> biomeWhitelist, List<String> modIdWhitelist, int terrainCheckRadius, int maxTerrainHeightDifference, boolean canSpawnGraveyardMobs) {
        this(true, spacing, separation, salt, biomeWhitelist, modIdWhitelist, terrainCheckRadius, maxTerrainHeightDifference, canSpawnGraveyardMobs);
    }

    private StructureConfigEntry(boolean enabled, int spacing, int separation, int salt, List<String> biomeWhitelist, List<String> modIdWhitelist, int terrainCheckRadius, int maxTerrainHeightDifference, boolean canSpawnGraveyardMobs) {
        this.enabled = enabled;
        this.spacing = spacing;
        this.separation = separation;
        this.salt = salt;
        this.biomeWhitelist = biomeWhitelist;
        this.modIdWhitelist = modIdWhitelist;
        this.terrainCheckRadius = terrainCheckRadius;
        this.maxTerrainHeightDifference = maxTerrainHeightDifference;
        this.canSpawnGraveyardMobs = canSpawnGraveyardMobs;
    }

    public static StructureConfigEntry of(int spacing, int separation, int salt, List<String> whitelist, List<String> modWhitelist, int terrainCheckRadius, int maxTerrainHeightDifference, boolean canSpawnGraveyardMobs) {
        return new StructureConfigEntry(spacing, separation, salt, whitelist, modWhitelist, terrainCheckRadius, maxTerrainHeightDifference, canSpawnGraveyardMobs);
    }

}
