package com.finallion.graveyard.world.structures;

import com.finallion.graveyard.init.TGStructures;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.structure.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.feature.JigsawFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;

import java.util.Set;
import java.util.function.Predicate;

public abstract class StructureUtil {


    public static boolean checkForOtherStructures(ChunkGenerator generator, long seed, int chunkX, int chunkZ, int size) {
        StructureConfig village = generator.getStructuresConfig().getForType(StructureFeature.VILLAGE);
        StructureConfig grave = generator.getStructuresConfig().getForType(TGStructures.SMALL_GRAVE_STRUCTURE);
        StructureConfig tree = generator.getStructuresConfig().getForType(TGStructures.MEMORIAL_TREE_STRUCTURE);
        StructureConfig m_graveyard = generator.getStructuresConfig().getForType(TGStructures.MEDIUM_GRAVEYARD_STRUCTURE);
        StructureConfig house = generator.getStructuresConfig().getForType(TGStructures.HAUNTED_HOUSE_STRUCTURE);
        StructureConfig mushroom = generator.getStructuresConfig().getForType(TGStructures.MUSHROOM_GRAVE_STRUCTURE);



        for (int k = chunkX - 5; k <= chunkX + 5; ++k) {
            for (int m = chunkZ - 5; m <= chunkZ + 5; ++m) {
                if (village != null) {
                    ChunkPos possibleVillagePos = StructureFeature.VILLAGE.getStartChunk(village, seed, k, m);
                    if (k == possibleVillagePos.x && m == possibleVillagePos.z) {
                        return false;
                    }
                }

                if (grave != null) {
                    ChunkPos possibleVillagePos = TGStructures.SMALL_GRAVE_STRUCTURE.getStartChunk(grave, seed, k, m);
                    if (k == possibleVillagePos.x && m == possibleVillagePos.z) {
                        return false;
                    }
                }

                if (tree != null) {
                    ChunkPos possibleVillagePos = TGStructures.MEMORIAL_TREE_STRUCTURE.getStartChunk(tree, seed, k, m);
                    if (k == possibleVillagePos.x && m == possibleVillagePos.z) {
                        return false;
                    }
                }

                if (m_graveyard != null) {
                    ChunkPos possibleVillagePos = TGStructures.MEDIUM_GRAVEYARD_STRUCTURE.getStartChunk(m_graveyard, seed, k, m);
                    if (k == possibleVillagePos.x && m == possibleVillagePos.z) {
                        return false;
                    }
                }

                if (mushroom != null) {
                    ChunkPos possibleVillagePos = TGStructures.MUSHROOM_GRAVE_STRUCTURE.getStartChunk(mushroom, seed, k, m);
                    if (k == possibleVillagePos.x && m == possibleVillagePos.z) {
                        return false;
                    }
                }

                if (house != null) {
                    ChunkPos possibleVillagePos = TGStructures.HAUNTED_HOUSE_STRUCTURE.getStartChunk(house, seed, k, m);
                    if (k == possibleVillagePos.x && m == possibleVillagePos.z) {
                        return false;
                    }
                }
            }
        }
        return true;
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
        int offset = size;

        Set<Biome> biomesInAreaOne = generator.getBiomeSource().getBiomesInArea(chunkX, 0, chunkZ, size, generator.getMultiNoiseSampler());

        for (Biome biome : biomesInAreaOne) {
            if (biome.getCategory() == Biome.Category.OCEAN || biome.getCategory() == Biome.Category.RIVER || biome.getCategory() == Biome.Category.BEACH) {
                return false;
            }
        }

        return true;

    }

}
