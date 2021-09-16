package com.finallion.graveyard.structures;



import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.entity.EntityType;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

import java.util.List;


public class SmallWalledGraveyardDesert extends TGBaseStructure {

    public SmallWalledGraveyardDesert(Codec<DefaultFeatureConfig> codec) {
        super(codec, "small_walled_graveyard_desert", 1.5, 0);
    }

    private static final List<SpawnSettings.SpawnEntry> STRUCTURE_MONSTERS = ImmutableList.of(
            new SpawnSettings.SpawnEntry(EntityType.VINDICATOR, 1, 1, 2),
            new SpawnSettings.SpawnEntry(EntityType.PILLAGER, 1, 1, 2)
    );

    @Override
    public List<SpawnSettings.SpawnEntry> getMonsterSpawns() {
        return STRUCTURE_MONSTERS;
    }
}
