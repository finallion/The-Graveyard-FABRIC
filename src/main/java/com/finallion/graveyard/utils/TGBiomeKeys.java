package com.finallion.graveyard.utils;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

import java.util.ArrayList;
import java.util.List;

public class TGBiomeKeys {

    public static List<RegistryKey<Biome>> birch_biomes = new ArrayList<>();
    public static List<RegistryKey<Biome>> forest_biomes = new ArrayList<>();
    public static List<RegistryKey<Biome>> thick_forest_biomes = new ArrayList<>();

    public static void init() {
        // villages generate in plains, savanna, desert, taiga and snowy taiga

        birch_biomes.add(BiomeKeys.BIRCH_FOREST_HILLS);
        birch_biomes.add(BiomeKeys.TALL_BIRCH_HILLS);
        birch_biomes.add(BiomeKeys.BIRCH_FOREST);
        birch_biomes.add(BiomeKeys.TALL_BIRCH_FOREST);

        forest_biomes.add(BiomeKeys.FOREST);
        forest_biomes.add(BiomeKeys.DARK_FOREST_HILLS);
        forest_biomes.add(BiomeKeys.WOODED_HILLS);
        forest_biomes.add(BiomeKeys.WOODED_MOUNTAINS);
        forest_biomes.add(BiomeKeys.GIANT_SPRUCE_TAIGA);
        forest_biomes.add(BiomeKeys.GIANT_SPRUCE_TAIGA_HILLS);
        forest_biomes.add(BiomeKeys.GIANT_TREE_TAIGA);
        forest_biomes.add(BiomeKeys.GIANT_TREE_TAIGA_HILLS);

        thick_forest_biomes.add(BiomeKeys.TAIGA_HILLS);
        thick_forest_biomes.add(BiomeKeys.TAIGA_MOUNTAINS);
        thick_forest_biomes.add(BiomeKeys.SNOWY_TAIGA_HILLS);
        thick_forest_biomes.add(BiomeKeys.SNOWY_TAIGA_MOUNTAINS);
        thick_forest_biomes.add(BiomeKeys.DARK_FOREST);
        thick_forest_biomes.add(BiomeKeys.TAIGA);
        thick_forest_biomes.add(BiomeKeys.SNOWY_TAIGA);


    }


}
