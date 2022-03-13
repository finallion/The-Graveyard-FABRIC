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
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

import java.util.Arrays;
import java.util.Collections;

public class SmallDesertGraveStructure extends AbstractGraveyardStructure {

    public SmallDesertGraveStructure(Codec<StructurePoolFeatureConfig> codec) {
        super(codec, new StructureConfigEntry(20, 16, 681236914,
                        Arrays.asList("#" + Biome.Category.DESERT.getName()),
                        Collections.emptyList(), Arrays.asList("#minecraft", "#terralith"), false),
                4, 681236914, SmallDesertGraveGenerator.STARTING_POOL, "small_desert_grave");
    }

    @Override
    public ConfiguredStructureFeature<?, ?> getStructureFeature() {
        return TGConfiguredStructureFeatures.SMALL_DESERT_GRAVE_STRUCTURE_CONFIG.value();
    }

    public static class SmallDesertGraveGenerator {
        public static final RegistryEntry<StructurePool> STARTING_POOL;

        public SmallDesertGraveGenerator() {
        }

        public static void init() {
        }

        static {
            STARTING_POOL = StructurePools.register(new StructurePool(new Identifier(TheGraveyard.MOD_ID, "small_desert_grave"), new Identifier("empty"), ImmutableList.of(
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":small_desert_grave/small_desert_grave_01"), 1),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":small_desert_grave/small_desert_grave_02"), 1),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":small_desert_grave/small_desert_grave_03"), 1),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":small_desert_grave/small_desert_grave_04"), 1),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":small_desert_grave/small_desert_grave_05"), 1),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":small_desert_grave/small_desert_grave_06"), 1),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":small_desert_grave/small_desert_grave_07"), 1),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":small_desert_grave/small_desert_grave_08"), 1),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":small_desert_grave/small_desert_grave_09"), 1),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":small_desert_grave/small_desert_grave_10"), 1),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":small_desert_grave/small_desert_grave_11"), 1),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":small_desert_grave/small_desert_grave_12"), 1),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":small_desert_grave/small_desert_grave_13"), 1)
            ), StructurePool.Projection.RIGID));
        }
    }

}
