package com.finallion.graveyard.init;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.world.biomes.HauntedForestBiomes;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

public class TGBiomes {
    public static final RegistryKey<Biome> HAUNTED_FOREST_KEY = RegistryKey.of(Registry.BIOME_KEY, new Identifier(TheGraveyard.MOD_ID, "haunted_forest"));
    public static final RegistryKey<Biome> HAUNTED_FOREST_LAKE_KEY = RegistryKey.of(Registry.BIOME_KEY, new Identifier(TheGraveyard.MOD_ID, "haunted_lakes"));
    public static final RegistryKey<Biome> ERODED_HAUNTED_FOREST_KEY = RegistryKey.of(Registry.BIOME_KEY, new Identifier(TheGraveyard.MOD_ID, "eroded_haunted_forest"));



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

         */


    }
}
