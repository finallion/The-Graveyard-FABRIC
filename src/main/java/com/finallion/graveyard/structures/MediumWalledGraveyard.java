package com.finallion.graveyard.structures;


import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.entity.EntityType;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

import java.util.List;


public class MediumWalledGraveyard extends TGBaseStructure {


    public MediumWalledGraveyard(Codec<DefaultFeatureConfig> codec) {
        super(codec, "medium_walled_graveyard", 2, 0);
    }

    private static final List<SpawnSettings.SpawnEntry> STRUCTURE_MONSTERS = ImmutableList.of(
            new SpawnSettings.SpawnEntry(EntityType.ZOMBIE_VILLAGER, 1, 1, 4),
            new SpawnSettings.SpawnEntry(EntityType.ZOMBIE, 1, 2, 4)
    );



    @Override
    public List<SpawnSettings.SpawnEntry>  getMonsterSpawns() {
        return STRUCTURE_MONSTERS;
    }
}