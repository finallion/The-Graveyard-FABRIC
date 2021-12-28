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

public class MemorialTreeStructure extends StructureFeature<StructurePoolFeatureConfig> {
    private static final int SIZE = 10;

    public MemorialTreeStructure(Codec<StructurePoolFeatureConfig> codec) {
        super(codec, (context) -> {
                    if (!MemorialTreeStructure.canGenerate(context)) {
                        return Optional.empty();
                    }
                    else {
                        return MemorialTreeStructure.createPiecesGenerator(context);
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

        StructurePoolFeatureConfig newConfig = new StructurePoolFeatureConfig(() -> MemorialTreeStructure.MemorialTreeGenerator.STARTING_POOL, 10);

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

    public static class MemorialTreeGenerator {
        public static final StructurePool STARTING_POOL;

        public MemorialTreeGenerator() {
        }

        public static void init() {
        }

        static {
            STARTING_POOL = StructurePools.register(new StructurePool(new Identifier(TheGraveyard.MOD_ID, "memorial_tree"), new Identifier("empty"), ImmutableList.of(Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":memorial_tree/memorial_tree_01"), 1)), StructurePool.Projection.RIGID));
        }
    }

}
