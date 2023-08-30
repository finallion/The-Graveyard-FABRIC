package com.lion.graveyard.platform.forge;


import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;

public class BiomeModificationImpl {

    public static void addMobSpawn(TagKey<Biome> tag, SpawnGroup spawnGroup, EntityType<?> entityType, int weight, int minGroupSize, int maxGroupSize) {
        // added via biome modifier json
    }

    public static void addPlacedFeature(TagKey<Biome> tag, GenerationStep.Feature step, RegistryKey<PlacedFeature> featureKey) {
        // empty
    }
}
