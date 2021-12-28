package com.finallion.graveyard.world.structures;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.init.TGEntities;
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
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.feature.JigsawFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

import java.util.Optional;

public class MediumGraveyardStructure extends StructureFeature<StructurePoolFeatureConfig> {
    private static final int SIZE = 30;
    public static final Pool<SpawnSettings.SpawnEntry> MONSTER_SPAWNS = Pool.of(
            new SpawnSettings.SpawnEntry(TGEntities.SKELETON_CREEPER, 1, 1, 1),
            new SpawnSettings.SpawnEntry(TGEntities.GHOUL, 1, 1, 1));

    public MediumGraveyardStructure(Codec<StructurePoolFeatureConfig> codec) {
        super(codec, (context) -> {
                    if (!MediumGraveyardStructure.canGenerate(context)) {
                        return Optional.empty();
                    }
                    else {
                        return MediumGraveyardStructure.createPiecesGenerator(context);
                    }
                },
                PostPlacementProcessor.EMPTY);
    }

    private static boolean canGenerate(StructureGeneratorFactory.Context<StructurePoolFeatureConfig> context) {
        BlockPos centerOfChunk = new BlockPos(context.chunkPos().x * 16, 0, context.chunkPos().z * 16);

        if (!StructureUtil.isTerrainFlat(context.chunkGenerator(), centerOfChunk.getX(), centerOfChunk.getZ(), context.world(), SIZE)) {
            return false;
        }

        if (!StructureUtil.isWater(context.chunkGenerator(), centerOfChunk.getX(), centerOfChunk.getZ(), context.world(), SIZE)) {
            return false;
        }

        return true;
    }

    public static Optional<StructurePiecesGenerator<StructurePoolFeatureConfig>> createPiecesGenerator(StructureGeneratorFactory.Context<StructurePoolFeatureConfig> context) {
        BlockPos blockpos = context.chunkPos().getCenterAtY(0);

        StructurePoolFeatureConfig newConfig = new StructurePoolFeatureConfig(() -> MediumGraveyardStructure.MediumGraveyardGenerator.STARTING_POOL, 10);

        StructureGeneratorFactory.Context<StructurePoolFeatureConfig> newContext = new StructureGeneratorFactory.Context<>(
                context.chunkGenerator(),
                context.biomeSource(),
                context.seed(),
                context.chunkPos(),
                newConfig,
                context.world(),
                context.validBiome(),
                context.structureManager(),
                context.registryManager()
        );

        Optional<StructurePiecesGenerator<StructurePoolFeatureConfig>> structurePiecesGenerator =
                StructurePoolBasedGenerator.generate(
                        newContext,
                        PoolStructurePiece::new,
                        blockpos,
                        false,
                        true
                );


        return structurePiecesGenerator;
    }

    public static class MediumGraveyardGenerator {
        public static final StructurePool STARTING_POOL;

        public MediumGraveyardGenerator() {
        }

        public static void init() {
        }

        static {
            STARTING_POOL = StructurePools.register(new StructurePool(new Identifier(TheGraveyard.MOD_ID, "medium_graveyard"), new Identifier("empty"), ImmutableList.of(
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":medium_graveyard/medium_graveyard_01"), 1),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":medium_graveyard/medium_graveyard_02"), 1)
            ), StructurePool.Projection.RIGID));
        }
    }

}
