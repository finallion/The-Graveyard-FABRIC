package com.finallion.graveyard.world.structures;


import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.config.StructureConfigEntry;
import com.finallion.graveyard.init.TGConfiguredStructureFeatures;
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

public class MemorialTreeStructure {

    public static class MemorialTreeGenerator {
        public static final RegistryEntry<StructurePool> STARTING_POOL;

        public MemorialTreeGenerator() {
        }

        public static void init() {
        }

        static {
            STARTING_POOL = StructurePools.register(new StructurePool(new Identifier(TheGraveyard.MOD_ID, "memorial_tree"), new Identifier("empty"), ImmutableList.of(Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":memorial_tree/memorial_tree_01"), 1)), StructurePool.Projection.RIGID));
        }
    }

}

