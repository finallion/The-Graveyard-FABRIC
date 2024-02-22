package com.lion.graveyard.platform.fabric;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class BiomeModificationsImpl {

    public static void addMobSpawn(TagKey<Biome> tag, MobCategory spawnGroup, EntityType<?> entityType, int weight, int minGroupSize, int maxGroupSize) {
        BiomeModifications.addSpawn(biomeSelector -> biomeSelector.hasTag(tag) && biomeSelector.hasTag(BiomeTags.IS_OVERWORLD), spawnGroup, entityType, weight, minGroupSize, maxGroupSize);
    }

    public static void addPlacedFeature(TagKey<Biome> tag, GenerationStep.Decoration step, ResourceKey<PlacedFeature> featureKey) {
        BiomeModifications.addFeature(biomeSelector -> biomeSelector.hasTag(tag), step, featureKey);
    }
}
