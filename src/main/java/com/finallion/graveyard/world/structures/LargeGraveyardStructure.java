package com.finallion.graveyard.world.structures;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.init.TGProcessors;
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

public class LargeGraveyardStructure extends StructureFeature<StructurePoolFeatureConfig> {
    private static final int SIZE = 45;

    public LargeGraveyardStructure(Codec<StructurePoolFeatureConfig> codec) {
        super(codec, (context) -> {
                    if (!LargeGraveyardStructure.canGenerate(context)) {
                        return Optional.empty();
                    }
                    else {
                        return LargeGraveyardStructure.createPiecesGenerator(context);
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

        StructurePoolFeatureConfig newConfig = new StructurePoolFeatureConfig(() -> LargeGraveyardStructure.LargeGraveyardGenerator.STARTING_POOL,
                // sets how many structure pieces the jigsaw will generate outwards from the start pool
                3);

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
    public static class LargeGraveyardGenerator {
        public static final StructurePool STARTING_POOL;
        public static final StructurePool BRANCH_POOL;
        public static final StructurePool FEATURE_POOL;
        public static final StructurePool CRYPT_POOL;
        public static final StructurePool STREET_POOL;


        public LargeGraveyardGenerator() {
        }

        public static void init() {
        }

        static {
            STARTING_POOL = StructurePools.register(new StructurePool(new Identifier(TheGraveyard.MOD_ID, "large_walled_graveyard/start_pool"), new Identifier("empty"), ImmutableList.of(Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/graveyard_entrance"), 1)), StructurePool.Projection.RIGID));
            BRANCH_POOL = StructurePools.register(new StructurePool(new Identifier(TheGraveyard.MOD_ID, "large_walled_graveyard/branch_pool"), new Identifier("empty"), ImmutableList.of(
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/branch_pool/branch_large_grave_01"), 10),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/branch_pool/branch_spider_den"), 6),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/branch_pool/branch_lost_grave"), 10),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/branch_pool/branch_graves_01"), 10),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/branch_pool/branch_graves_02"), 10),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/branch_pool/branch_graves_03"), 10),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/branch_pool/branch_graves_04"), 10),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/branch_pool/branch_graves_05"), 10),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/branch_pool/branch_looted_graves_01"), 10),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/branch_pool/branch_looted_graves_02"), 10),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/branch_pool/branch_willow"), 6),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/branch_pool/branch_large_grave_02"), 10),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/branch_pool/branch_boulder_01"), 9),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/branch_pool/branch_boulder_02"), 10),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/branch_pool/branch_boulder_03"), 10),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/branch_pool/branch_nature_01"), 15),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/branch_pool/branch_nature_02"), 15),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/branch_pool/branch_nature_03"), 15),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/branch_pool/branch_nature_04"), 15)
            ), StructurePool.Projection.RIGID));
            FEATURE_POOL = StructurePools.register(new StructurePool(new Identifier(TheGraveyard.MOD_ID, "large_walled_graveyard/feature_pool"), new Identifier("empty"), ImmutableList.of(
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/feature_pool/wither_skeleton_mill"), 1),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/feature_pool/gallows_hill"), 1),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/feature_pool/walled_graves_with_crypt"), 2),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/feature_pool/walled_street_01"), 2),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/feature_pool/walled_street_02"), 2),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/feature_pool/walled_street_03"), 2)
            ), StructurePool.Projection.RIGID));
            CRYPT_POOL = StructurePools.register(new StructurePool(new Identifier(TheGraveyard.MOD_ID, "large_walled_graveyard/small_crypt_pool"), new Identifier("empty"), ImmutableList.of(
                    Pair.of(StructurePoolElement.ofProcessedSingle(TheGraveyard.MOD_ID + ":large_graveyard/small_crypt_pool/small_crypt", TGProcessors.WATERLOGGED_LIST), 1)
            ), StructurePool.Projection.RIGID));
            STREET_POOL = StructurePools.register(new StructurePool(new Identifier(TheGraveyard.MOD_ID, "large_walled_graveyard/street_pool"), new Identifier("empty"), ImmutableList.of(
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/street_pool/street_01"), 5),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/street_pool/street_02"), 5),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/street_pool/street_03"), 5),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/street_pool/street_04"), 5),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/street_pool/street_05"), 4),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/street_pool/street_06"), 4),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/street_pool/street_07"), 4),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/street_pool/street_08"), 4),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/street_pool/street_09"), 4),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/street_pool/corner_01"), 7),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/street_pool/corner_02"), 7),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/street_pool/corner_03"), 7),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/street_pool/corner_04"), 7),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/street_pool/corner_05"), 7),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/street_pool/corner_06"), 7),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/street_pool/crossroad_01"), 6),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/street_pool/crossroad_02"), 6),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/street_pool/street_end_01"), 4),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/street_pool/street_end_02"), 4),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":large_graveyard/street_pool/street_end_03"), 4)
            ), StructurePool.Projection.RIGID));
        }
    }

}
