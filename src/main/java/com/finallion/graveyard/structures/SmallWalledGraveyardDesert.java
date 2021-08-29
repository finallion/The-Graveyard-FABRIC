package com.finallion.graveyard.structures;



import com.mojang.serialization.Codec;
import net.minecraft.entity.EntityType;
import net.minecraft.util.collection.Pool;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;


public class SmallWalledGraveyardDesert extends TGBaseStructure {

    public SmallWalledGraveyardDesert(Codec<DefaultFeatureConfig> codec) {
        super(codec, "small_walled_graveyard_desert", 1.5, 0);
    }

    private static final Pool<SpawnSettings.SpawnEntry> STRUCTURE_MONSTERS = Pool.of(
            new SpawnSettings.SpawnEntry(EntityType.VINDICATOR, 1, 1, 2),
            new SpawnSettings.SpawnEntry(EntityType.PILLAGER, 1, 1, 2)
    );

    @Override
    public Pool<SpawnSettings.SpawnEntry> getMonsterSpawns() {
        return STRUCTURE_MONSTERS;
    }
}
