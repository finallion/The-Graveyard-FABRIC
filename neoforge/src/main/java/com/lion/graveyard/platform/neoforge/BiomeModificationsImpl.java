package com.lion.graveyard.platform.neoforge;


import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class BiomeModificationsImpl {

    public static void addMobSpawn(TagKey<Biome> tag, MobCategory spawnGroup, EntityType<?> entityType, int weight, int minGroupSize, int maxGroupSize) {
        // added via biome modifier json
    }

    public static void addPlacedFeature(TagKey<Biome> tag, GenerationStep.Decoration step, ResourceKey<PlacedFeature> featureKey) {
        // empty
    }
}