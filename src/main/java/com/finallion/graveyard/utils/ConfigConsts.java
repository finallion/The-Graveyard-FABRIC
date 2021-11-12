package com.finallion.graveyard.utils;

import com.finallion.graveyard.TheGraveyard;
import net.minecraft.util.Identifier;

public class ConfigConsts {

    public static final boolean enableFogForest = TheGraveyard.config.getBiome(new Identifier(TheGraveyard.MOD_ID, "haunted_forest_biome")).biomeFogSpawn;
    public static final boolean enableFogLakes = TheGraveyard.config.getBiome(new Identifier(TheGraveyard.MOD_ID, "haunted_lakes_biome")).biomeFogSpawn;
    public static final boolean enableFogEroded = TheGraveyard.config.getBiome(new Identifier(TheGraveyard.MOD_ID, "eroded_haunted_forest_biome")).biomeFogSpawn;



    public static final float fogDensityForest = TheGraveyard.config.getBiome(new Identifier(TheGraveyard.MOD_ID, "haunted_forest_biome")).fogDensity;
    public static final float fogDensityLakes = TheGraveyard.config.getBiome(new Identifier(TheGraveyard.MOD_ID, "haunted_lakes_biome")).fogDensity;
    public static final float fogDensityEroded = TheGraveyard.config.getBiome(new Identifier(TheGraveyard.MOD_ID, "eroded_haunted_forest_biome")).fogDensity;

    public static final boolean enableForestBiome = TheGraveyard.config.getBiome(new Identifier(TheGraveyard.MOD_ID, "haunted_forest_biome")).biomeSpawn;
    public static final boolean enableLakesBiome = TheGraveyard.config.getBiome(new Identifier(TheGraveyard.MOD_ID, "haunted_lakes_biome")).biomeSpawn;
    public static final boolean enableErodedBiome = TheGraveyard.config.getBiome(new Identifier(TheGraveyard.MOD_ID, "eroded_haunted_forest_biome")).biomeSpawn;

    public static final float chanceForest = TheGraveyard.config.getBiome(new Identifier(TheGraveyard.MOD_ID, "haunted_forest_biome")).biomeChance;
    public static final float chanceLakes = TheGraveyard.config.getBiome(new Identifier(TheGraveyard.MOD_ID, "haunted_lakes_biome")).biomeChance;
    public static final float chanceEroded = TheGraveyard.config.getBiome(new Identifier(TheGraveyard.MOD_ID, "eroded_haunted_forest_biome")).biomeChance;

    public static final int fogForestMaxY = TheGraveyard.config.getBiome(new Identifier(TheGraveyard.MOD_ID, "haunted_forest_biome")).fogMaxY;
    public static final int fogForestMinY = TheGraveyard.config.getBiome(new Identifier(TheGraveyard.MOD_ID, "haunted_forest_biome")).fogMinY;
    public static final int fogLakesMaxY = TheGraveyard.config.getBiome(new Identifier(TheGraveyard.MOD_ID, "haunted_lakes_biome")).fogMaxY;
    public static final int fogLakesMinY = TheGraveyard.config.getBiome(new Identifier(TheGraveyard.MOD_ID, "haunted_lakes_biome")).fogMinY;
    public static final int fogErodedMaxY = TheGraveyard.config.getBiome(new Identifier(TheGraveyard.MOD_ID, "eroded_haunted_forest_biome")).fogMaxY;
    public static final int fogErodedMinY = TheGraveyard.config.getBiome(new Identifier(TheGraveyard.MOD_ID, "eroded_haunted_forest_biome")).fogMinY;





}
