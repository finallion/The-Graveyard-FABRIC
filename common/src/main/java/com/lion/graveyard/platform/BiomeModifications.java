package com.lion.graveyard.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class BiomeModifications {

    @ExpectPlatform
    public static void addMobSpawn(TagKey<Biome> tag, MobCategory spawnGroup, EntityType<?> entityType, int weight, int minGroupSize, int maxGroupSize) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void addPlacedFeature(TagKey<Biome> tag, GenerationStep.Decoration step, ResourceKey<PlacedFeature> featureKey) {
        throw new AssertionError();
    }

}
