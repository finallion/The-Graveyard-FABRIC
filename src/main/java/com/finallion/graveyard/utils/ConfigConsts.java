package com.finallion.graveyard.utils;

import com.finallion.graveyard.TheGraveyard;
import net.minecraft.util.Identifier;

public class ConfigConsts {

    public static final boolean enableFogForest = TheGraveyard.config.biomeFogSpawn(new Identifier(TheGraveyard.MOD_ID, "graveyard_biome_fog_forest"));
    public static final boolean enableFogLakes = TheGraveyard.config.biomeFogSpawn(new Identifier(TheGraveyard.MOD_ID, "graveyard_biome_fog_lakes"));
    public static final boolean enableFogEroded = TheGraveyard.config.biomeFogSpawn(new Identifier(TheGraveyard.MOD_ID, "graveyard_biome_fog_eroded"));

    public static final float fogDensityForest = TheGraveyard.config.getBiomeFog(new Identifier(TheGraveyard.MOD_ID, "graveyard_biome_fog_forest")).density;
    public static final float fogDensityLakes = TheGraveyard.config.getBiomeFog(new Identifier(TheGraveyard.MOD_ID, "graveyard_biome_fog_lakes")).density;
    public static final float fogDensityEroded = TheGraveyard.config.getBiomeFog(new Identifier(TheGraveyard.MOD_ID, "graveyard_biome_fog_eroded")).density;

    public static final boolean enableForestBiome = TheGraveyard.config.additionalEnabled(new Identifier(TheGraveyard.MOD_ID, "haunted_forest_biome"));
    public static final boolean enableLakesBiome = TheGraveyard.config.additionalEnabled(new Identifier(TheGraveyard.MOD_ID, "haunted_lakes_biome"));
    public static final boolean enableErodedBiome = TheGraveyard.config.additionalEnabled(new Identifier(TheGraveyard.MOD_ID, "eroded_haunted_forest_biome"));

}
