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
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

import java.util.Arrays;
import java.util.Collections;

public class CryptStructure extends AbstractUndergroundStructure {

    public CryptStructure(Codec<StructurePoolFeatureConfig> codec) {
        super(new StructureConfigEntry(14, 12, 893183913,
                        Arrays.asList("#underground"),
                        Collections.emptyList(), Arrays.asList("#minecraft", "#terralith"), false),
                "crypt");
    }

    @Override
    public ConfiguredStructureFeature<?, ?> getStructureFeature() {
        return TGConfiguredStructureFeatures.CRYPT_STRUCTURE_CONFIG.value();
    }


    public static class CryptGenerator {
        public static final RegistryEntry<StructurePool> CORRIDOR_POOL;
        public static final RegistryEntry<StructurePool> STARTING_POOL;
        //public static final RegistryEntry<StructurePool> TREASURE_POOL;

        public CryptGenerator() {
        }

        public static void init() {
        }

        static {
            STARTING_POOL = StructurePools.register(new StructurePool(new Identifier(TheGraveyard.MOD_ID, "crypt/start_pool"), new Identifier("empty"), ImmutableList.of(Pair.of(StructurePoolElement.ofProcessedSingle(TheGraveyard.MOD_ID + ":crypt/start_pool/start_room", TGProcessors.WATERLOGGED_LIST), 1)), StructurePool.Projection.RIGID));
            CORRIDOR_POOL = StructurePools.register(new StructurePool(new Identifier(TheGraveyard.MOD_ID, "crypt/corridor_pool"), new Identifier("minecraft:empty"), ImmutableList.of(
                    Pair.of(StructurePoolElement.ofProcessedSingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/room_01", TGProcessors.WATERLOGGED_LIST), 10),
                    Pair.of(StructurePoolElement.ofProcessedSingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/room_02", TGProcessors.WATERLOGGED_LIST), 10),
                    Pair.of(StructurePoolElement.ofProcessedSingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/room_03", TGProcessors.WATERLOGGED_LIST), 10),
                    Pair.of(StructurePoolElement.ofProcessedSingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/room_04", TGProcessors.WATERLOGGED_LIST), 10),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/corner_01"), 10),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/corner_02"), 10),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/corner_03"), 10),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/corridor_01"), 10),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/corridor_02"), 10),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/corridor_03"), 10),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/corridor_04"), 10),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/corridor_05"), 10),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/corridor_06"), 10),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/corridor_07"), 10),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/corridor_08"), 10),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/corridor_09"), 10),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/corridor_10"), 10),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/corridor_11"), 10),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/corridor_12"), 10),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/corridor_13"), 10),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/stair_01"), 5),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/stair_02"), 10),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/stair_03"), 10),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/labyrinth_01"), 7),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/library_01"), 10),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/library_02"), 10),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/trap_01"), 10),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/trap_02"), 5),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/trap_03"), 5),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/end_01"), 8),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/end_02"), 8),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/end_03"), 8),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/end_04"), 8),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/end_05"), 8),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":crypt/corridor_pool/end_06"), 8)
            ), StructurePool.Projection.RIGID));
            //TREASURE_POOL = StructurePools.register(new StructurePool(new Identifier(TheGraveyard.MOD_ID, "crypt/treasure_pool"), new Identifier("empty"), ImmutableList.of(Pair.of(
            //        StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":crypt/treasure_pool/treasure_01"), 1)), StructurePool.Projection.RIGID));

        }
    }

}