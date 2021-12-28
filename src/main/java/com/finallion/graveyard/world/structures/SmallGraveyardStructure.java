package com.finallion.graveyard.world.structures;

import com.finallion.graveyard.TheGraveyard;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.PostPlacementProcessor;
import net.minecraft.structure.StructureGeneratorFactory;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.JigsawFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

import java.util.Optional;

public class SmallGraveyardStructure extends StructureFeature<StructurePoolFeatureConfig> {
    private static final int SIZE = 15;

    public SmallGraveyardStructure(Codec<StructurePoolFeatureConfig> codec) {
        super(codec, (context) -> {
                    if (!SmallGraveyardStructure.canGenerate(context)) {
                        return Optional.empty();
                    }
                    else {
                        return SmallGraveyardStructure.createPiecesGenerator(context);
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

        StructurePoolFeatureConfig newConfig = new StructurePoolFeatureConfig(() -> SmallGraveyardStructure.SmallGraveyardGenerator.STARTING_POOL, 10);

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

    public static class SmallGraveyardGenerator {
        public static final StructurePool STARTING_POOL;

        public SmallGraveyardGenerator() {
        }

        public static void init() {
        }

        static {
            STARTING_POOL = StructurePools.register(new StructurePool(new Identifier(TheGraveyard.MOD_ID, "small_graveyard"), new Identifier("empty"), ImmutableList.of(
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":small_graveyard/small_graveyard_01"), 1),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":small_graveyard/small_graveyard_02"), 1),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":small_graveyard/small_graveyard_03"), 1),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":small_graveyard/small_graveyard_04"), 1),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":small_graveyard/small_graveyard_05"), 1),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":small_graveyard/small_graveyard_06"), 1),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":small_graveyard/small_graveyard_07"), 1),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":small_graveyard/small_graveyard_08"), 1),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":small_graveyard/small_graveyard_09"), 1)),
                    StructurePool.Projection.RIGID));
        }
    }

}
