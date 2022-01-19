package com.finallion.graveyard.world.structures;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.config.StructureConfigEntry;
import com.finallion.graveyard.init.TGEntities;
import com.finallion.graveyard.init.TGStructures;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.entity.EntityType;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.PostPlacementProcessor;
import net.minecraft.structure.StructureGeneratorFactory;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.JigsawFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

public class SmallDesertGraveyardStructure extends AbstractGraveyardStructure {
    public static final Pool<SpawnSettings.SpawnEntry> MONSTER_SPAWNS = Pool.of(
            new SpawnSettings.SpawnEntry(EntityType.PILLAGER, 10, 1, 1),
            new SpawnSettings.SpawnEntry(EntityType.VINDICATOR, 1, 1, 1));

    public SmallDesertGraveyardStructure(Codec<StructurePoolFeatureConfig> codec) {
        super(codec, new StructureConfigEntry(32, 28, 598017285,
                        Arrays.asList(Biome.Category.DESERT.getName()),
                        Collections.emptyList()),
                20, 598017285, SmallDesertGraveyardGenerator.STARTING_POOL, "small_desert_graveyard");
    }

    @Override
    public ConfiguredStructureFeature<?, ?> getStructureFeature() {
        return TGStructures.SMALL_DESERT_GRAVEYARD_STRUCTURE_CONFIG;
    }

    public static class SmallDesertGraveyardGenerator {
        public static final StructurePool STARTING_POOL;

        public SmallDesertGraveyardGenerator() {
        }

        public static void init() {
        }

        static {
            STARTING_POOL = StructurePools.register(new StructurePool(new Identifier(TheGraveyard.MOD_ID, "small_desert_graveyard"), new Identifier("empty"), ImmutableList.of(Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":small_desert_graveyard/small_desert_graveyard_01"), 1)), StructurePool.Projection.RIGID));
        }
    }

}
