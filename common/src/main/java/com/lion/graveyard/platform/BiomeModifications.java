package com.lion.graveyard.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;

public class BiomeModifications {

    @ExpectPlatform
    public static void addMobSpawn(TagKey<Biome> tag, SpawnGroup spawnGroup, EntityType<?> entityType, int weight, int minGroupSize, int maxGroupSize) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void addPlacedFeature(TagKey<Biome> tag, GenerationStep.Feature step, RegistryKey<PlacedFeature> featureKey) {
        throw new AssertionError();
    }

}
