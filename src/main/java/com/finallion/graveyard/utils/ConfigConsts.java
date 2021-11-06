package com.finallion.graveyard.utils;

import com.finallion.graveyard.TheGraveyard;
import net.minecraft.util.Identifier;

public class ConfigConsts {

    public static boolean enableFog = TheGraveyard.config.biomeFogSpawn(new Identifier(TheGraveyard.MOD_ID, "graveyard_biome_fog"));
    public static float fogDensity = TheGraveyard.config.getBiomeFog(new Identifier(TheGraveyard.MOD_ID, "graveyard_biome_fog")).density;
}
