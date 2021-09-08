package com.finallion.graveyard.structures;


import com.mojang.serialization.Codec;
import net.minecraft.entity.EntityType;
import net.minecraft.util.collection.Pool;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;



public class MediumWalledGraveyard extends TGBaseStructure {


    public MediumWalledGraveyard(Codec<DefaultFeatureConfig> codec) {
        super(codec, "medium_walled_graveyard", 3, 0);
    }

    private static final Pool<SpawnSettings.SpawnEntry> STRUCTURE_MONSTERS = Pool.of(
            new SpawnSettings.SpawnEntry(EntityType.ZOMBIE_VILLAGER, 1, 1, 4),
            new SpawnSettings.SpawnEntry(EntityType.ZOMBIE, 1, 2, 4)
    );

    @Override
    public Pool<SpawnSettings.SpawnEntry> getMonsterSpawns() {
        return STRUCTURE_MONSTERS;
    }
}