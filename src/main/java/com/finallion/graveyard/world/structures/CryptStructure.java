package com.finallion.graveyard.world.structures;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.config.StructureConfigEntry;
import com.finallion.graveyard.init.TGConfiguredStructureFeatures;
import com.finallion.graveyard.init.TGProcessors;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryEntry;


import java.util.Arrays;
import java.util.Collections;

public class CryptStructure {

    public static class CryptGenerator {
        public static final RegistryEntry<StructurePool> CORRIDOR_POOL;
        public static final RegistryEntry<StructurePool> STARTING_POOL;

        public CryptGenerator() {
        }

        public static void init() {
        }

        static {
            STARTING_POOL = StructurePools.register(new StructurePool(new Identifier(TheGraveyard.MOD_ID, "crypt/start_pool"), new Identifier("empty"), ImmutableList.of(Pair.of(StructurePoolElement.ofProcessedSingle(TheGraveyard.MOD_ID + ":crypt/start_pool/start_room", TGProcessors.WATERLOGGED_CRYPT_LIST), 1)), StructurePool.Projection.RIGID));
            CORRIDOR_POOL = StructurePools.register(new StructurePool(new Identifier(TheGraveyard.MOD_ID, "crypt/corridor_pool"), new Identifier("minecraft:empty"), ImmutableList.of(
                    Pair.of(StructurePoolElement.ofProcessedSingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/room_01", TGProcessors.WATERLOGGED_CRYPT_LIST), 10),
                    Pair.of(StructurePoolElement.ofProcessedSingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/room_02", TGProcessors.WATERLOGGED_CRYPT_LIST), 10),
                    Pair.of(StructurePoolElement.ofProcessedSingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/room_03", TGProcessors.WATERLOGGED_CRYPT_LIST), 10),
                    Pair.of(StructurePoolElement.ofProcessedSingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/room_04", TGProcessors.WATERLOGGED_CRYPT_LIST), 10),
                    Pair.of(StructurePoolElement.ofProcessedSingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/corner_01", TGProcessors.WATERLOGGED_CRYPT_LIST), 10),
                    Pair.of(StructurePoolElement.ofProcessedSingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/corner_02", TGProcessors.WATERLOGGED_CRYPT_LIST), 10),
                    Pair.of(StructurePoolElement.ofProcessedSingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/corner_03", TGProcessors.WATERLOGGED_CRYPT_LIST), 10),
                    Pair.of(StructurePoolElement.ofProcessedSingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/corridor_01", TGProcessors.WATERLOGGED_CRYPT_LIST), 10),
                    Pair.of(StructurePoolElement.ofProcessedSingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/corridor_02", TGProcessors.WATERLOGGED_CRYPT_LIST), 10),
                    Pair.of(StructurePoolElement.ofProcessedSingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/corridor_03", TGProcessors.WATERLOGGED_CRYPT_LIST), 10),
                    Pair.of(StructurePoolElement.ofProcessedSingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/corridor_04", TGProcessors.WATERLOGGED_CRYPT_LIST), 10),
                    Pair.of(StructurePoolElement.ofProcessedSingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/corridor_05", TGProcessors.WATERLOGGED_CRYPT_LIST), 10),
                    Pair.of(StructurePoolElement.ofProcessedSingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/corridor_06", TGProcessors.WATERLOGGED_CRYPT_LIST), 10),
                    Pair.of(StructurePoolElement.ofProcessedSingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/corridor_07", TGProcessors.WATERLOGGED_CRYPT_LIST), 10),
                    Pair.of(StructurePoolElement.ofProcessedSingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/corridor_08", TGProcessors.WATERLOGGED_CRYPT_LIST), 10),
                    Pair.of(StructurePoolElement.ofProcessedSingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/corridor_09", TGProcessors.WATERLOGGED_CRYPT_LIST), 10),
                    Pair.of(StructurePoolElement.ofProcessedSingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/corridor_10", TGProcessors.WATERLOGGED_CRYPT_LIST), 10),
                    Pair.of(StructurePoolElement.ofProcessedSingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/corridor_11", TGProcessors.WATERLOGGED_CRYPT_LIST), 10),
                    Pair.of(StructurePoolElement.ofProcessedSingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/corridor_12", TGProcessors.WATERLOGGED_CRYPT_LIST), 10),
                    Pair.of(StructurePoolElement.ofProcessedSingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/corridor_13", TGProcessors.WATERLOGGED_CRYPT_LIST), 10),
                    Pair.of(StructurePoolElement.ofProcessedSingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/stair_01", TGProcessors.WATERLOGGED_CRYPT_LIST), 5),
                    Pair.of(StructurePoolElement.ofProcessedSingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/stair_02", TGProcessors.WATERLOGGED_CRYPT_LIST), 10),
                    Pair.of(StructurePoolElement.ofProcessedSingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/stair_03", TGProcessors.WATERLOGGED_CRYPT_LIST), 10),
                    Pair.of(StructurePoolElement.ofProcessedSingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/labyrinth_01", TGProcessors.WATERLOGGED_CRYPT_LIST), 7),
                    Pair.of(StructurePoolElement.ofProcessedSingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/library_01", TGProcessors.WATERLOGGED_CRYPT_LIST), 10),
                    Pair.of(StructurePoolElement.ofProcessedSingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/library_02", TGProcessors.WATERLOGGED_CRYPT_LIST), 10),
                    Pair.of(StructurePoolElement.ofProcessedSingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/trap_01", TGProcessors.WATERLOGGED_CRYPT_LIST), 10),
                    Pair.of(StructurePoolElement.ofProcessedSingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/trap_02", TGProcessors.WATERLOGGED_CRYPT_LIST), 5),
                    Pair.of(StructurePoolElement.ofProcessedSingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/trap_03", TGProcessors.WATERLOGGED_CRYPT_LIST), 5),
                    Pair.of(StructurePoolElement.ofProcessedSingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/end_01", TGProcessors.WATERLOGGED_CRYPT_LIST), 8),
                    Pair.of(StructurePoolElement.ofProcessedSingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/end_02", TGProcessors.WATERLOGGED_CRYPT_LIST), 8),
                    Pair.of(StructurePoolElement.ofProcessedSingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/end_03", TGProcessors.WATERLOGGED_CRYPT_LIST), 8),
                    Pair.of(StructurePoolElement.ofProcessedSingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/end_04", TGProcessors.WATERLOGGED_CRYPT_LIST), 8),
                    Pair.of(StructurePoolElement.ofProcessedSingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/end_05", TGProcessors.WATERLOGGED_CRYPT_LIST), 8),
                    Pair.of(StructurePoolElement.ofProcessedSingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/end_06", TGProcessors.WATERLOGGED_CRYPT_LIST), 8)
            ), StructurePool.Projection.RIGID));
        }
    }

}
