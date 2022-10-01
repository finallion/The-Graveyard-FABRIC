package com.finallion.graveyard.world.structures;


import com.finallion.graveyard.TheGraveyard;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryEntry;


public class RuinsStructure {

    public static class RuinsGenerator {
        public static final RegistryEntry<StructurePool> STARTING_POOL;

        public RuinsGenerator() {
        }

        public static void init() {
        }

        static {
            STARTING_POOL = StructurePools.register(new StructurePool(new Identifier(TheGraveyard.MOD_ID, "ruins"), new Identifier("empty"), ImmutableList.of(
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":ruins/ruins_01"), 1),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":ruins/ruins_02"), 1),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":ruins/ruins_03"), 1)
            ), StructurePool.Projection.RIGID));
        }
    }

}

