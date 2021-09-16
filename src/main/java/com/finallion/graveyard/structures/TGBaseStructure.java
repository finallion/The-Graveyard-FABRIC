package com.finallion.graveyard.structures;

import com.finallion.graveyard.TheGraveyard;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.structure.MarginedStructureStart;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

import java.util.Set;

public class TGBaseStructure extends StructureFeature<DefaultFeatureConfig> {
    private final int SUNKEN_IN;
    private final double SIZE;
    private final String NAME;
    private int averageHeight;
    private final int MAX_WATER_HITS = 8;
    private final int MAX_TERRAIN_DIFFERENCE_BASE = 2;
    private final int MAX_TERRAIN_DIFFERENCE_LG = 5;
    private final int OFFSET_WATER_CHECK_LG = 25;

    public TGBaseStructure(Codec<DefaultFeatureConfig> codec, String name, double size, int sunkenIn) {
        super(codec);
        this.SUNKEN_IN = sunkenIn;
        this.SIZE = size;
        this.NAME = name;

    }


    @Override
    public StructureStartFactory<DefaultFeatureConfig> getStructureStartFactory() {
        return TGBaseStructure.Start::new;
    }


    @Override
    protected boolean shouldStartAt(ChunkGenerator chunkGenerator, BiomeSource biomeSource, long seed, ChunkRandom chunkRandom, int chunkX, int chunkZ, Biome biome, ChunkPos chunkPos, DefaultFeatureConfig featureConfig) {
        BlockPos centerOfChunk = new BlockPos(chunkPos.x * 16, 0, chunkPos.z * 16);


        if (!isTerrainFlat(chunkGenerator, centerOfChunk.getX(), centerOfChunk.getZ())) {
            return false;
        }

        if (!isWater(chunkGenerator, centerOfChunk.getX(), centerOfChunk.getZ(), centerOfChunk)) {
            return false;
        }

        if (!checkForOtherStructures(chunkGenerator, seed, chunkRandom, centerOfChunk.getX(), centerOfChunk.getZ())) {
            return false;
        }

        return true;
    }

    protected boolean isWater(ChunkGenerator generator, int chunkX, int chunkZ, BlockPos centerOfChunk) {
        // center of generation is chunkX 0 chunkZ (i)
        // checks:
        //
        // n    j    l
        // o    i    k
        // q    p    m


        int offset = (int) SIZE * 8;

        // checks are in a larger radius if the structure spawns above water
        // needed for the large graveyard to make ugly generation over rivers and in oceans less likely
        // additionally checks are for oceans
        if (SIZE > 2) {
            offset += OFFSET_WATER_CHECK_LG;

            Set<Biome> biomesInAreaOne = generator.getBiomeSource().getBiomesInArea(chunkX, 0, chunkZ, 156);
            Set<Biome> biomesInAreaTwo = generator.getBiomeSource().getBiomesInArea(chunkX, 0, chunkZ, 28);

            for (Biome biome : biomesInAreaOne) {
                if (biome.getCategory() == Biome.Category.OCEAN) {
                    return false;
                }
            }

            // clears the inner circle of generation from water sources
            // following checks clear the outer circle from possible water
            for (Biome biome : biomesInAreaTwo) {
                if (biome.getCategory() == Biome.Category.RIVER) {
                    return false;
                }
            }

        }


        int i1 = generator.getHeightInGround(chunkX, chunkZ, Heightmap.Type.WORLD_SURFACE_WG);
        int j1 = generator.getHeightInGround(chunkX, chunkZ + offset, Heightmap.Type.WORLD_SURFACE_WG);
        int k1 = generator.getHeightInGround(chunkX + offset, chunkZ, Heightmap.Type.WORLD_SURFACE_WG);
        int l1 = generator.getHeightInGround(chunkX + offset, chunkZ + offset, Heightmap.Type.WORLD_SURFACE_WG);
        int m1 = generator.getHeightInGround(chunkX - offset, chunkZ + offset, Heightmap.Type.WORLD_SURFACE_WG);
        int n1 = generator.getHeightInGround(chunkX + offset, chunkZ - offset, Heightmap.Type.WORLD_SURFACE_WG);
        int o1 = generator.getHeightInGround(chunkX, chunkZ - offset, Heightmap.Type.WORLD_SURFACE_WG);
        int p1 = generator.getHeightInGround(chunkX - offset, chunkZ, Heightmap.Type.WORLD_SURFACE_WG);
        int q1 = generator.getHeightInGround(chunkX - offset, chunkZ - offset, Heightmap.Type.WORLD_SURFACE_WG);

        BlockView vi1 = generator.getColumnSample(chunkX, chunkZ);
        BlockView vj1 = generator.getColumnSample(chunkX, chunkZ + offset);
        BlockView vk1 = generator.getColumnSample(chunkX + offset, chunkZ);
        BlockView vl1 = generator.getColumnSample(chunkX + offset, chunkZ + offset);
        BlockView vm1 = generator.getColumnSample(chunkX - offset, chunkZ + offset);
        BlockView vn1 = generator.getColumnSample(chunkX + offset, chunkZ - offset);
        BlockView vo1 = generator.getColumnSample(chunkX, chunkZ - offset);
        BlockView vp1 = generator.getColumnSample(chunkX - offset, chunkZ);
        BlockView vq1 = generator.getColumnSample(chunkX - offset, chunkZ - offset);

        BlockState bi1 = vi1.getBlockState(centerOfChunk.up(i1));
        BlockState bj1 = vj1.getBlockState(centerOfChunk.up(j1));
        BlockState bk1 = vk1.getBlockState(centerOfChunk.up(k1));
        BlockState bl1 = vl1.getBlockState(centerOfChunk.up(l1));
        BlockState bm1 = vm1.getBlockState(centerOfChunk.up(m1));
        BlockState bn1 = vn1.getBlockState(centerOfChunk.up(n1));
        BlockState bo1 = vo1.getBlockState(centerOfChunk.up(o1));
        BlockState bp1 = vp1.getBlockState(centerOfChunk.up(p1));
        BlockState bq1 = vq1.getBlockState(centerOfChunk.up(q1));

        /*
        System.out.println("Water hits results: ");
        System.out.println("One: " + bi1 + " Height: " + i1 + " at: " + chunkX + " " + chunkZ);
        System.out.println("Two: " + bj1 + " Height: " + j1 + " at: " + chunkX + " " + (chunkZ + offset));
        System.out.println("Three: " + bk1 + " Height: " + k1 + " at: " + (chunkX + offset) + " " + chunkZ);
        System.out.println("Four: " + bl1 + " Height: " + l1 + " at: " + (chunkX + offset) + " " + (chunkZ + offset));
        System.out.println("Five: " + bm1 + " Height: " + m1 + " at: " + (chunkX - offset) + " " + (chunkZ + offset));
        System.out.println("Six: " + bn1 + " Height: " + n1 + " at: " + (chunkX + offset) + " " + (chunkZ - offset));
        System.out.println("Seven: " + bo1 + " Height: " + o1 + " at: " + chunkX + " " + (chunkZ - offset));
        System.out.println("Eight: " + bp1 + " Height: " + p1 + " at: " + (chunkX - offset) + " " + chunkZ);
        System.out.println("Nine: " + bq1 + " Height: " + q1 + " at: " + (chunkX - offset) + " " + (chunkZ - offset));
        */

        return countWaterMatches(bi1, bj1, bk1, bl1, bm1, bn1, bo1, bq1, bp1);

    }

    private boolean countWaterMatches(BlockState... blockStates) {
        // counts how many blockstates are in water
        // if there are more than two or equal to four return true and allow structure to spawn
        // if more checks are added the threshold number should be raised
        int count = 0;
        for (BlockState blockState : blockStates) {
            count += (blockState.getFluidState().isEmpty() ? 1 : 0);
        }

        return count >= MAX_WATER_HITS;
    }


    protected boolean isTerrainFlat(ChunkGenerator generator, int chunkX, int chunkZ) {
        // center of generation is chunkX 0 chunkZ (i)
        // checks:
        //
        // n    j    l
        // o    i    k
        // q    p    m

        int offset = (int) SIZE * 8;

        int i1 = generator.getHeight(chunkX, chunkZ, Heightmap.Type.WORLD_SURFACE_WG);
        int j1 = generator.getHeight(chunkX, chunkZ + offset, Heightmap.Type.WORLD_SURFACE_WG);
        int k1 = generator.getHeight(chunkX + offset, chunkZ, Heightmap.Type.WORLD_SURFACE_WG);
        int l1 = generator.getHeight(chunkX + offset, chunkZ + offset, Heightmap.Type.WORLD_SURFACE_WG);
        int m1 = generator.getHeight(chunkX - offset, chunkZ + offset, Heightmap.Type.WORLD_SURFACE_WG);
        int n1 = generator.getHeight(chunkX + offset, chunkZ - offset, Heightmap.Type.WORLD_SURFACE_WG);
        int o1 = generator.getHeight(chunkX, chunkZ - offset, Heightmap.Type.WORLD_SURFACE_WG);
        int p1 = generator.getHeight(chunkX - offset, chunkZ, Heightmap.Type.WORLD_SURFACE_WG);
        int q1 = generator.getHeight(chunkX - offset, chunkZ - offset, Heightmap.Type.WORLD_SURFACE_WG);

        /*
        System.out.println("Terrain flatness results: ");
        System.out.println("One: " + " Height: " + i1 + " at: " + chunkX + " " + chunkZ);
        System.out.println("Two: " + " Height: " + j1 + " at: " + chunkX + " " + (chunkZ + offset));
        System.out.println("Three: " + " Height: " + k1 + " at: " + (chunkX + offset) + " " + chunkZ);
        System.out.println("Four: " + " Height: " + l1 + " at: " + (chunkX + offset) + " " + (chunkZ + offset));
        System.out.println("Five: " + " Height: " + m1 + " at: " + (chunkX - offset) + " " + (chunkZ + offset));
        System.out.println("Six: " + " Height: " + n1 + " at: " + (chunkX + offset) + " " + (chunkZ - offset));
        System.out.println("Seven: " + " Height: " + o1 + " at: " + chunkX + " " + (chunkZ - offset));
        System.out.println("Eight: " + " Height: " + p1 + " at: " + (chunkX - offset) + " " + chunkZ);
        System.out.println("Nine: " + " Height: " + q1 + " at: " + (chunkX - offset) + " " + (chunkZ - offset));
         */

        int minCorners = Math.min(Math.min(n1, m1), Math.min(q1, l1));
        int minSides = Math.min(Math.min(j1, p1), Math.min(o1, k1));
        int minHeight = Math.min(Math.min(minCorners, minSides), i1);

        int maxCorners = Math.max(Math.max(n1, m1), Math.max(q1, l1));
        int maxSides = Math.max(Math.max(j1, p1), Math.max(o1, k1));
        int maxHeight = Math.max(Math.max(maxCorners, maxSides), i1);

        averageHeight = Math.abs((maxHeight + minHeight) / 2);

        if (SIZE > 2) {
            return Math.abs(maxHeight - minHeight) <= MAX_TERRAIN_DIFFERENCE_LG;
        }

        return Math.abs(maxHeight - minHeight) <= MAX_TERRAIN_DIFFERENCE_BASE;
    }


    public boolean checkForOtherStructures(ChunkGenerator generator, long seed, ChunkRandom rand, int chunkX, int chunkZ) {
        StructureConfig structureConfig = generator.getStructuresConfig().getForType(StructureFeature.VILLAGE);
        if (structureConfig == null) {
            return false;
        } else {
            if (SIZE <= 2) {
                return true;
            }


            int blocksAwayToCheck = 15;
            for (int k = chunkX - blocksAwayToCheck; k <= chunkX + blocksAwayToCheck; ++k) {
                for (int l = chunkZ - blocksAwayToCheck; l <= chunkZ + blocksAwayToCheck; ++l) {
                    ChunkPos chunkPos = StructureFeature.VILLAGE.getStartChunk(structureConfig, seed, rand, k, l);
                    if (k == chunkPos.x && l == chunkPos.z) {
                        return false;
                    }
                }
            }

            return true;
        }
    }

    private int getSunkenIn() {
        return SUNKEN_IN;
    }

    private double getSize() {
        return SIZE;
    }

    private String getStructureName() {
        return NAME;
    }

    private int getAverageHeight() {
        return averageHeight;
    }


    public static class Start extends MarginedStructureStart<DefaultFeatureConfig> {
        private final int SUNKEN_IN;
        private final double SIZE;
        private final String NAME;
        private int averageHeight;


        public Start(StructureFeature<DefaultFeatureConfig> structureIn, int chunkX, int chunkZ, BlockBox blockBox, int referenceIn, long seedIn, String name, double size, int sunkenIn, int averageHeight) {
            super(structureIn, chunkX, chunkZ, blockBox, referenceIn, seedIn);
            this.SIZE = size;
            this.SUNKEN_IN = sunkenIn;
            this.NAME = name;
            this.averageHeight = averageHeight;

        }


        public Start(StructureFeature<DefaultFeatureConfig> structureIn, int chunkX, int chunkZ, BlockBox blockBox, int referenceIn, long seed) {
            this(structureIn, chunkX, chunkZ, blockBox, referenceIn, seed, ((TGBaseStructure) structureIn).getStructureName(), ((TGBaseStructure) structureIn).getSize(), ((TGBaseStructure) structureIn).getSunkenIn(), ((TGBaseStructure) structureIn).getAverageHeight());

        }

        @Override
        public void init(DynamicRegistryManager dynamicRegistryManager, ChunkGenerator chunkGenerator, StructureManager structureManager, int chunkX, int chunkZ, Biome biome, DefaultFeatureConfig defaultFeatureConfig) {
            int x = chunkX * 16;
            int z = chunkZ * 16;

            BlockPos.Mutable centerPos = new BlockPos.Mutable(x, averageHeight, z);

            StructurePoolBasedGenerator.method_30419(
                    dynamicRegistryManager,
                    new StructurePoolFeatureConfig(() -> dynamicRegistryManager.get(Registry.TEMPLATE_POOL_WORLDGEN)
                            .get(new Identifier(TheGraveyard.MOD_ID, NAME + "/start_pool")),
                            10),
                    PoolStructurePiece::new,
                    chunkGenerator,
                    structureManager,
                    centerPos,
                    this.children,
                    this.random,
                    false,
                    false);


            //this.randomUpwardTranslation(random, -SUNKEN_IN, -SUNKEN_IN + 1);
            this.children.forEach(piece -> piece.translate(0, 1, 0));
            this.children.forEach(piece -> piece.getBoundingBox().move(0, SUNKEN_IN, 0));

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
