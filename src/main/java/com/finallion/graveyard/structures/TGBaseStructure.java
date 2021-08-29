package com.finallion.graveyard.structures;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.init.TGStructures;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.structure.MarginedStructureStart;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TGBaseStructure extends StructureFeature<DefaultFeatureConfig> {
    private final int sunkenIn;
    private final double size;
    private final String name;
    private int averageHeight;

    public TGBaseStructure(Codec<DefaultFeatureConfig> codec, String name, double size, int sunkenIn) {
        super(codec);
        this.sunkenIn = sunkenIn;
        this.size = size;
        this.name = name;
    }



    @Override
    public StructureStartFactory<DefaultFeatureConfig> getStructureStartFactory() {
        return TGBaseStructure.Start::new;
    }

    @Override
    protected boolean shouldStartAt(ChunkGenerator chunkGenerator, BiomeSource biomeSource, long seed, ChunkRandom chunkRandom, ChunkPos chunkPos, Biome biome, ChunkPos chunkPos2, DefaultFeatureConfig featureConfig, HeightLimitView heightLimitView) {
        BlockPos centerOfChunk = new BlockPos(chunkPos.x * 16, 0, chunkPos.z * 16);

        if (!isTerrainFlat(chunkGenerator, centerOfChunk.getX(), centerOfChunk.getZ(), heightLimitView)) {
            return false;
        }

        if (!isWater(chunkGenerator, centerOfChunk.getX(), centerOfChunk.getZ(), heightLimitView, centerOfChunk)) {
            return false;
        }

        /*
        if (!checkForOtherStructures(chunkGenerator, seed, chunkRandom, centerOfChunk.getX(), centerOfChunk.getZ())) {
            return false;
        }

         */

        return true;
    }



    protected boolean isWater(ChunkGenerator generator, int chunkX, int chunkZ, HeightLimitView heightLimitView, BlockPos centerOfChunk) {
        int offset = (int) size * 8;

        int i1 = generator.getHeightInGround(chunkX, chunkZ, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView);
        int j1 = generator.getHeightInGround(chunkX, chunkZ + offset, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView);
        int k1 = generator.getHeightInGround(chunkX + offset, chunkZ, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView);
        int l1 = generator.getHeightInGround(chunkX + offset, chunkZ + offset, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView);

        VerticalBlockSample vi1 = generator.getColumnSample(chunkX, chunkZ, heightLimitView);
        VerticalBlockSample vj1 = generator.getColumnSample(chunkX, chunkZ, heightLimitView);
        VerticalBlockSample vk1 = generator.getColumnSample(chunkX, chunkZ, heightLimitView);
        VerticalBlockSample vl1 = generator.getColumnSample(chunkX, chunkZ, heightLimitView);

        BlockState bi1 = vi1.getState(centerOfChunk.up(i1));
        BlockState bj1 = vj1.getState(centerOfChunk.up(j1));
        BlockState bk1 = vk1.getState(centerOfChunk.up(k1));
        BlockState bl1 = vl1.getState(centerOfChunk.up(l1));

        return bi1.getFluidState().isEmpty() && bj1.getFluidState().isEmpty() && bk1.getFluidState().isEmpty() && bl1.getFluidState().isEmpty();
    }



    protected boolean isTerrainFlat(ChunkGenerator generator, int chunkX, int chunkZ, HeightLimitView heightLimitView) {
        int offset = (int) size * 8;

        int i1 = generator.getHeight(chunkX, chunkZ, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView);
        int j1 = generator.getHeight(chunkX, chunkZ + offset, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView);
        int k1 = generator.getHeight(chunkX + offset, chunkZ, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView);
        int l1 = generator.getHeight(chunkX + offset, chunkZ + offset, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView);
        int minHeight = Math.min(Math.min(i1, j1), Math.min(k1, l1));
        int maxHeight = Math.max(Math.max(i1, j1), Math.max(k1, l1));
        averageHeight = Math.abs((maxHeight + minHeight) / 2);

        return Math.abs(maxHeight - minHeight) <= 2;
    }


    public boolean checkForOtherStructures(ChunkGenerator generator, long seed, ChunkRandom rand, int chunkX, int chunkZ) {
        StructureConfig configVillage = generator.getStructuresConfig().getForType(StructureFeature.VILLAGE);
        StructureConfig configMediumGraveyard = generator.getStructuresConfig().getForType(TGStructures.MEDIUM_WALLED_GRAVEYARD);
        StructureConfig configSmallGraveyard = generator.getStructuresConfig().getForType(TGStructures.SMALL_WALLED_GRAVEYARD);
        StructureConfig configSmallGraveyardSavanna = generator.getStructuresConfig().getForType(TGStructures.SMALL_WALLED_GRAVEYARD_SAVANNA);
        StructureConfig configMushroomGrave = generator.getStructuresConfig().getForType(TGStructures.MUSHROOM_GRAVE);
        StructureConfig configLargeBirch = generator.getStructuresConfig().getForType(TGStructures.LARGE_BIRCH_TREE);
        StructureConfig configSmallGrave = generator.getStructuresConfig().getForType(TGStructures.SMALL_GRAVE);

        for (int k = chunkX - 6; k <= chunkX + 6; ++k) {
            for (int m = chunkZ - 6; m <= chunkZ + 6; ++m) {
                if (configVillage != null) {
                    ChunkPos possibleVillagePos = StructureFeature.VILLAGE.getStartChunk(configVillage, seed, rand, k, m);
                    if (k == possibleVillagePos.x && m == possibleVillagePos.z) {
                        return false;
                    }
                }
                if (configSmallGraveyardSavanna != null) {
                    ChunkPos possibleSmallGraveyardSavannaPos = TGStructures.SMALL_WALLED_GRAVEYARD_SAVANNA.getStartChunk(configSmallGraveyardSavanna, seed, rand, k, m);
                    if (k == possibleSmallGraveyardSavannaPos.x && m == possibleSmallGraveyardSavannaPos.z && this != TGStructures.SMALL_WALLED_GRAVEYARD_SAVANNA) {
                        return false;
                    }
                }
                if (configMushroomGrave != null) {
                    ChunkPos possibleMushroomGravePos = TGStructures.MUSHROOM_GRAVE.getStartChunk(configMushroomGrave, seed, rand, k, m);
                    if (k == possibleMushroomGravePos.x && m == possibleMushroomGravePos.z && this != TGStructures.MUSHROOM_GRAVE) {
                        return false;
                    }
                }
                if (configLargeBirch != null) {
                    ChunkPos possibleLargeBirchPos = TGStructures.LARGE_BIRCH_TREE.getStartChunk(configLargeBirch, seed, rand, k, m);
                    if (k == possibleLargeBirchPos.x && m == possibleLargeBirchPos.z && this != TGStructures.LARGE_BIRCH_TREE) {
                        return false;
                    }
                }
                if (configSmallGrave != null) {
                    ChunkPos possibleSmallGravePos = TGStructures.SMALL_GRAVE.getStartChunk(configSmallGrave, seed, rand, k, m);
                    if (k == possibleSmallGravePos.x && m == possibleSmallGravePos.z && this != TGStructures.SMALL_GRAVE) {
                        return false;
                    }
                }
                if (configSmallGraveyard != null) {
                    ChunkPos possibleSmallGraveyardPos = TGStructures.SMALL_WALLED_GRAVEYARD.getStartChunk(configSmallGraveyard, seed, rand, k, m);
                    if (k == possibleSmallGraveyardPos.x && m == possibleSmallGraveyardPos.z && this != TGStructures.SMALL_WALLED_GRAVEYARD) {
                        return false;
                    }
                }
                if (configMediumGraveyard != null) {
                    ChunkPos possibleMediumGraveyardPos = TGStructures.MEDIUM_WALLED_GRAVEYARD.getStartChunk(configMediumGraveyard, seed, rand, k, m);
                    if (k == possibleMediumGraveyardPos.x && m == possibleMediumGraveyardPos.z && this != TGStructures.MEDIUM_WALLED_GRAVEYARD) {
                        return false;
                    }
                }
            }

        }
        return true;
    }

    private int getSunkenIn() {
        return sunkenIn;
    }

    private double getSize() {
        return size;
    }

    private String getStructureName() {
        return name;
    }

    private int getAverageHeight() {
        return averageHeight;
    }


    public static class Start extends MarginedStructureStart<DefaultFeatureConfig> {
        private final int sunkenIn;
        private final double size;
        private final String name;
        private int averageHeight;


        public Start(StructureFeature<DefaultFeatureConfig> structureIn, ChunkPos chunkPos, int referenceIn, long seedIn) {
            this(structureIn, chunkPos, referenceIn, seedIn, ((TGBaseStructure) structureIn).getStructureName(), ((TGBaseStructure) structureIn).getSize(), ((TGBaseStructure) structureIn).getSunkenIn(), ((TGBaseStructure) structureIn).getAverageHeight());
        }

        public Start(StructureFeature<DefaultFeatureConfig> structureIn, ChunkPos chunkPos, int referenceIn, long seedIn, String name, double size, int sunkenIn, int averageHeight) {
            super(structureIn, chunkPos, referenceIn, seedIn);
            this.size = size;
            this.sunkenIn = sunkenIn;
            this.name = name;
            this.averageHeight = averageHeight;

        }


        @Override
        public void init(DynamicRegistryManager dynamicRegistryManager, ChunkGenerator chunkGenerator, StructureManager structureManager, ChunkPos chunkPos, Biome biome, DefaultFeatureConfig defaultFeatureConfig, HeightLimitView heightLimitView) {
            int x = chunkPos.x * 16;
            int z = chunkPos.z * 16;
            BlockPos.Mutable centerPos = new BlockPos.Mutable(x, averageHeight, z);

            StructurePoolFeatureConfig structureSettingsAndStartPool = new StructurePoolFeatureConfig(() -> dynamicRegistryManager.get(Registry.STRUCTURE_POOL_KEY)
                    .get(new Identifier(TheGraveyard.MOD_ID, name + "/start_pool")),
                    10);

            StructurePoolBasedGenerator.method_30419(
                    dynamicRegistryManager,
                    structureSettingsAndStartPool,
                    PoolStructurePiece::new,
                    chunkGenerator,
                    structureManager,
                    centerPos,
                    this,
                    this.random,
                    false,
                    // if the structure starts at given y value (false) or surface height (true)
                    false,
                    heightLimitView);


            this.translateUpward(-sunkenIn);
            this.children.forEach(piece -> piece.translate(0, 1, 0));
            this.children.forEach(piece -> piece.getBoundingBox().move(0, sunkenIn, 0));


            Vec3i structureCenter = this.children.get(0).getBoundingBox().getCenter();
            int xOffset = centerPos.getX() - structureCenter.getX();
            int zOffset = centerPos.getZ() - structureCenter.getZ();
            for (StructurePiece structurePiece : this.children) {
                structurePiece.translate(xOffset, 0, zOffset);
            }


            this.setBoundingBoxFromChildren();


        }


    }
}
