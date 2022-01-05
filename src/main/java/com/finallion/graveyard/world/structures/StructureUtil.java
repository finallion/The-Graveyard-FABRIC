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


}
