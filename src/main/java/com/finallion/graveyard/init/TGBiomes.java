package com.finallion.graveyard.init;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.world.biomes.HauntedForestBiomes;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

public class TGBiomes {
    public static final RegistryKey<Biome> ERODED_HAUNTED_FOREST_KEY = registerBiomeKeys("eroded_haunted_forest");
    public static final RegistryKey<Biome> HAUNTED_LAKES_KEY = registerBiomeKeys("haunted_lakes");

    private static RegistryKey<Biome> registerBiomeKeys(String name) {
        return RegistryKey.of(Registry.BIOME_KEY, new Identifier(TheGraveyard.MOD_ID, name));
    }

    public static void registerBiomes() {
        register(ERODED_HAUNTED_FOREST_KEY, HauntedForestBiomes.createErodedHauntedForest());
        register(HAUNTED_LAKES_KEY, HauntedForestBiomes.createHauntedLakes());
    }

    private static Biome register(RegistryKey<Biome> key, Biome biome) {
        return BuiltinRegistries.add(BuiltinRegistries.BIOME, key, biome);
    }


    /*
    public static void registerBiomes() {
        Registry.register(BuiltinRegistries.BIOME, HAUNTED_FOREST_KEY.getValue(), HauntedForestBiomes.createHauntedForest());
        Registry.register(BuiltinRegistries.BIOME, HAUNTED_FOREST_LAKE_KEY.getValue(), HauntedForestBiomes.createHauntedLakes());
        Registry.register(BuiltinRegistries.BIOME, ERODED_HAUNTED_FOREST_KEY.getValue(), HauntedForestBiomes.createErodedHauntedForest());


        /*

        if (ConfigConsts.enableForestBiome) {
            OverworldBiomes.addContinentalBiome(HAUNTED_FOREST_KEY, OverworldClimate.TEMPERATE, ConfigConsts.chanceForest);
        }

        if (ConfigConsts.enableLakesBiome) {
            OverworldBiomes.addContinentalBiome(HAUNTED_FOREST_LAKE_KEY, OverworldClimate.TEMPERATE, ConfigConsts.chanceLakes);
        }

        if (ConfigConsts.enableErodedBiome) {
            OverworldBiomes.addContinentalBiome(ERODED_HAUNTED_FOREST_KEY, OverworldClimate.TEMPERATE, ConfigConsts.chanceEroded);
        }

    }

     */
}
