package com.finallion.graveyard.utils;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

import java.util.ArrayList;
import java.util.List;

public class TGBiomeKeys {

    public static List<RegistryKey<Biome>> birch_biomes = new ArrayList<>();
    public static List<RegistryKey<Biome>> plain_biomes = new ArrayList<>();

    public static void init() {
        birch_biomes.add(net.minecraft.world.biome.BiomeKeys.BIRCH_FOREST);
        birch_biomes.add(net.minecraft.world.biome.BiomeKeys.BIRCH_FOREST_HILLS);
        birch_biomes.add(net.minecraft.world.biome.BiomeKeys.TALL_BIRCH_FOREST);
        birch_biomes.add(net.minecraft.world.biome.BiomeKeys.TALL_BIRCH_HILLS);

        plain_biomes.add(BiomeKeys.BIRCH_FOREST);
        plain_biomes.add(BiomeKeys.FOREST);
        plain_biomes.add(BiomeKeys.TAIGA);
        plain_biomes.add(BiomeKeys.SNOWY_TAIGA);
        plain_biomes.add(BiomeKeys.PLAINS);
        plain_biomes.add(BiomeKeys.TALL_BIRCH_FOREST);
        plain_biomes.add(BiomeKeys.DARK_FOREST);
        plain_biomes.add(BiomeKeys.SUNFLOWER_PLAINS);

    }


}
