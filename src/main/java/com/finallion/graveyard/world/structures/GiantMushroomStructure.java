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

public class GiantMushroomStructure {

    public static class GiantMushroomGenerator {
        public static final RegistryEntry<StructurePool> STARTING_POOL;
        public static final RegistryEntry<StructurePool> BROWN_CAP;
        public static final RegistryEntry<StructurePool> RED_CAP;

        public GiantMushroomGenerator() {
        }

        public static void init() {
        }

        static {
            STARTING_POOL = StructurePools.register(new StructurePool(new Identifier(TheGraveyard.MOD_ID, "giant_mushroom"), new Identifier("empty"), ImmutableList.of(
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":giant_mushroom/giant_brown_mushroom_stem_01"), 1),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":giant_mushroom/giant_red_mushroom_stem_01"), 1)
            ), StructurePool.Projection.RIGID));
            BROWN_CAP = StructurePools.register(new StructurePool(new Identifier(TheGraveyard.MOD_ID, "giant_mushroom/brown_cap"), new Identifier("empty"), ImmutableList.of(Pair.of(
                    StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":giant_mushroom/brown_cap/giant_brown_mushroom_cap_01"), 1)), StructurePool.Projection.RIGID));
            RED_CAP = StructurePools.register(new StructurePool(new Identifier(TheGraveyard.MOD_ID, "giant_mushroom/red_cap"), new Identifier("empty"), ImmutableList.of(Pair.of(
                    StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":giant_mushroom/red_cap/giant_red_mushroom_cap_01"), 1)), StructurePool.Projection.RIGID));

        }
    }

}

