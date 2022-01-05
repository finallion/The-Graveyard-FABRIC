package com.finallion.graveyard.world.structures;

import com.finallion.graveyard.config.StructureConfigEntry;
import com.mojang.serialization.Codec;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.PostPlacementProcessor;
import net.minecraft.structure.StructureGeneratorFactory;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructureConfig;
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
        BlockPos centerOfChunk = new BlockPos(context.chunkPos().x * 16, 0, context.chunkPos().z * 16);

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

        int offset = size;


        int i1 = generator.getHeight(chunkX, chunkZ, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView);
        int j1 = generator.getHeight(chunkX, chunkZ + offset, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView);
        int k1 = generator.getHeight(chunkX + offset, chunkZ, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView);
        //int l1 = generator.getHeight(chunkX + offset, chunkZ + offset, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView);
        //int m1 = generator.getHeight(chunkX - offset, chunkZ + offset, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView);
        //int n1 = generator.getHeight(chunkX + offset, chunkZ - offset, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView);
        int o1 = generator.getHeight(chunkX, chunkZ - offset, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView);
        int p1 = generator.getHeight(chunkX - offset, chunkZ, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView);
        //int q1 = generator.getHeight(chunkX - offset, chunkZ - offset, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView);

        /*
        System.out.println("Terrain flatness results: ");
        System.out.println("One: " + " Height: " + i1 + " at: " + chunkX + " " + chunkZ);
        System.out.println("Two: " + " Height: " + j1 + " at: " + chunkX + " " + (chunkZ + offset));
        System.out.println("Three: " + " Height: " + k1 + " at: " + (chunkX + offset) + " " + chunkZ);
        //System.out.println("Four: " + " Height: " + l1 + " at: " + (chunkX + offset) + " " + (chunkZ + offset));
        //System.out.println("Five: " + " Height: " + m1 + " at: " + (chunkX - offset) + " " + (chunkZ + offset));
        //System.out.println("Six: " + " Height: " + n1 + " at: " + (chunkX + offset) + " " + (chunkZ - offset));
        System.out.println("Seven: " + " Height: " + o1 + " at: " + chunkX + " " + (chunkZ - offset));
        System.out.println("Eight: " + " Height: " + p1 + " at: " + (chunkX - offset) + " " + chunkZ);
        //System.out.println("Nine: " + " Height: " + q1 + " at: " + (chunkX - offset) + " " + (chunkZ - offset));

         */


        //int minCorners = Math.min(Math.min(n1, m1), Math.min(q1, l1));
        int minSides = Math.min(Math.min(j1, p1), Math.min(o1, k1));
        //int minHeight = Math.min(Math.min(minCorners, minSides), i1);
        int minHeight = Math.min(minSides, i1);

        //int maxCorners = Math.max(Math.max(n1, m1), Math.max(q1, l1));
        int maxSides = Math.max(Math.max(j1, p1), Math.max(o1, k1));
        //int maxHeight = Math.max(Math.max(maxCorners, maxSides), i1);
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
