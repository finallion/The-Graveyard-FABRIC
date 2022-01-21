package com.finallion.graveyard.world.structures;

import com.finallion.graveyard.config.StructureConfigEntry;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.PostPlacementProcessor;
import net.minecraft.structure.StructureGeneratorFactory;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

import java.util.Optional;
import java.util.Set;

public abstract class AbstractGraveyardStructure extends StructureFeature<StructurePoolFeatureConfig> {
    private int seed;
    private final StructureConfigEntry config;
    private String structureName;


    public AbstractGraveyardStructure(Codec<StructurePoolFeatureConfig> codec, StructureConfigEntry config, int size, int seed, StructurePool pool, String name) {
        super(codec, (context) -> {
                    if (!AbstractGraveyardStructure.canGenerate(context, size)) {
                        return Optional.empty();
                    }
                    else {
                        return AbstractGraveyardStructure.createPiecesGenerator(context, pool);
                    }
                },
                PostPlacementProcessor.EMPTY);
        this.config = config;
        this.seed = seed;
        this.structureName = name;
    }


    public StructureConfigEntry getStructureConfigEntry() {
        return config;
    }


    public StructureConfig getStructureFeatureConfiguration() {
        return new StructureConfig(config.spacing, config.separation, config.salt);
    }


    public abstract ConfiguredStructureFeature<?, ?> getStructureFeature();

    public String getStructureName() {
        return structureName;
    }


    private static boolean canGenerate(StructureGeneratorFactory.Context<StructurePoolFeatureConfig> context, int size) {
        BlockPos centerOfChunk = context.chunkPos().getCenterAtY(0);

        if (!isTerrainFlat(context.chunkGenerator(), centerOfChunk.getX(), centerOfChunk.getZ(), context.world(), size)) {
            return false;
        }

        if (!isWater(context.chunkGenerator(), centerOfChunk.getX(), centerOfChunk.getZ(), context.world(), size)) {
            return false;
        }


        return true;
    }

    public static Optional<StructurePiecesGenerator<StructurePoolFeatureConfig>> createPiecesGenerator(StructureGeneratorFactory.Context<StructurePoolFeatureConfig> context, StructurePool pool) {
        BlockPos blockpos = context.chunkPos().getCenterAtY(0);

        StructurePoolFeatureConfig newConfig = new StructurePoolFeatureConfig(() -> pool, 3);

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

    protected static boolean isTerrainFlat(ChunkGenerator generator, int chunkX, int chunkZ, HeightLimitView heightLimitView, int size) {
        // center of generation is chunkX 0 chunkZ (i)
        // checks:
        //
        // n    j    l
        // o    i    k
        // q    p    m


        int i1 = generator.getHeightInGround(chunkX, chunkZ, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView);
        int j1 = generator.getHeightInGround(chunkX, chunkZ + size, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView);
        int k1 = generator.getHeightInGround(chunkX + size, chunkZ, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView);
        int o1 = generator.getHeightInGround(chunkX, chunkZ - size, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView);
        int p1 = generator.getHeightInGround(chunkX - size, chunkZ, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView);

        VerticalBlockSample sample1 = generator.getColumnSample(chunkX, chunkZ, heightLimitView);
        VerticalBlockSample sample2 = generator.getColumnSample(chunkX, chunkZ + size, heightLimitView);
        VerticalBlockSample sample3 = generator.getColumnSample(chunkX + size, chunkZ, heightLimitView);
        VerticalBlockSample sample4 = generator.getColumnSample(chunkX, chunkZ - size, heightLimitView);
        VerticalBlockSample sample5 = generator.getColumnSample(chunkX - size, chunkZ, heightLimitView);

        if (sample1.getState(i1).getFluidState().isIn(FluidTags.WATER) || sample2.getState(j1).getFluidState().isIn(FluidTags.WATER) || sample3.getState(k1).getFluidState().isIn(FluidTags.WATER) || sample4.getState(o1).getFluidState().isIn(FluidTags.WATER) || sample5.getState(p1).getFluidState().isIn(FluidTags.WATER)) {
            return false;
        }


        int minSides = Math.min(Math.min(j1, p1), Math.min(o1, k1));
        int minHeight = Math.min(minSides, i1);

        int maxSides = Math.max(Math.max(j1, p1), Math.max(o1, k1));
        int maxHeight = Math.max(maxSides, i1);

        return Math.abs(maxHeight - minHeight) <= 3;

    }

    protected static boolean isWater(ChunkGenerator generator, int chunkX, int chunkZ, HeightLimitView heightLimitView, int size) {
        Set<Biome> biomesInAreaOne = generator.getBiomeSource().getBiomesInArea(chunkX, 0, chunkZ, size, generator.getMultiNoiseSampler());

        for (Biome biome : biomesInAreaOne) {
            if (biome.getCategory() == Biome.Category.OCEAN || biome.getCategory() == Biome.Category.RIVER || biome.getCategory() == Biome.Category.BEACH) {
                return false;
            }
        }

        return true;

    }
}
