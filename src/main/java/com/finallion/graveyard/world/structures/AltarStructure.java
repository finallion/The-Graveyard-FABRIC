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


public class AltarStructure {

    public static class AltarGenerator {
        public static final RegistryEntry<StructurePool> STARTING_POOL;

        public AltarGenerator() {
        }

        public static void init() {
        }

        static {
            STARTING_POOL = StructurePools.register(new StructurePool(new Identifier(TheGraveyard.MOD_ID, "altar"), new Identifier("empty"), ImmutableList.of(Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":altar/altar_01"), 1)), StructurePool.Projection.RIGID));
        }
    }

}

