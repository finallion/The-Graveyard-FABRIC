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

public class MediumGraveyardStructure extends AbstractGraveyardStructure {

    public MediumGraveyardStructure(Codec<StructurePoolFeatureConfig> codec) {
        super(codec, new StructureConfigEntry(18, 16, 1690192399,
                        Arrays.asList("minecraft:forest", "minecraft:flower_forest", "terralith:brushland", "terralith:blooming_valley", "terralith:temperate_highlands", "graveyard_biomes:eroded_haunted_forest"),
                        Collections.emptyList(),
                        Arrays.asList("#minecraft", "#graveyard_biomes", "#terralith"), true), // blacklist birch and dark forest
                30, 1690192399, MediumGraveyardGenerator.STARTING_POOL, "medium_graveyard");
    }

    @Override
    public ConfiguredStructureFeature<?, ?> getStructureFeature() {
        return null;
        //return TGConfiguredStructureFeatures.MEDIUM_GRAVEYARD_STRUCTURE_CONFIG.value();
    }


    public static class MediumGraveyardGenerator {
        public static final RegistryEntry<StructurePool> STARTING_POOL;

        public MediumGraveyardGenerator() {
        }

        public static void init() {
        }

        static {
            STARTING_POOL = StructurePools.register(new StructurePool(new Identifier(TheGraveyard.MOD_ID, "medium_graveyard"), new Identifier("empty"), ImmutableList.of(
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":medium_graveyard/medium_graveyard_01"), 1),
                    Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":medium_graveyard/medium_graveyard_02"), 1)
            ), StructurePool.Projection.RIGID));
        }
    }



}
