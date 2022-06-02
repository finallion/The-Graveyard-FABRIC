package com.finallion.graveyard.world.structures;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.init.TGProcessors;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.pool.StructurePool.Projection;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryEntry;

import java.util.Optional;

public class HauntedHouseStructure {

    public static class HauntedHouseGenerator {
        public static final RegistryEntry<StructurePool> STARTING_POOL;
        public static final RegistryEntry<StructurePool> DOWNSTAIRS;

        public HauntedHouseGenerator() {}

        public static void init() {}

        // change from legacy single to processed single
        // if water is removed from blockentities (like chests) it might give the error message "Tried access block entity before it was created"
        static {
            STARTING_POOL = StructurePools.register(new StructurePool(new Identifier(TheGraveyard.MOD_ID, "haunted_house/start_pool"), new Identifier("empty"), ImmutableList.of(Pair.of(
                    StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":haunted_house/start_pool/haunted_house_01"), 1)), StructurePool.Projection.RIGID));
            DOWNSTAIRS = StructurePools.register(new StructurePool(new Identifier(TheGraveyard.MOD_ID, "haunted_house/downstairs_pool"), new Identifier("empty"), ImmutableList.of(Pair.of(
                    StructurePoolElement.ofProcessedSingle(TheGraveyard.MOD_ID + ":haunted_house/downstairs_pool/haunted_house_downstairs", TGProcessors.WATERLOGGED_LIST), 1)), StructurePool.Projection.RIGID));
        }
    }
}
/*
import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.config.StructureConfigEntry;
import com.finallion.graveyard.init.TGProcessors;
import com.finallion.graveyard.init.TGConfiguredStructureFeatures;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.PostPlacementProcessor;
import net.minecraft.structure.StructureGeneratorFactory;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

public class HauntedHouseStructure extends AbstractGraveyardStructure {


    public HauntedHouseStructure(Codec<StructurePoolFeatureConfig> codec) {
        super(codec, new StructureConfigEntry(25, 20, 451235912,
                        Arrays.asList("minecraft:dark_forest", "#" + Biome.Category.SWAMP.getName(), "terralith:moonlight_valley", "terralith:cloud_forest", "graveyard_biomes:haunted_lakes"),
                        Collections.emptyList(),
                        Arrays.asList("#minecraft", "#graveyard_biomes", "#terralith"),false), // only allow in swamp and dark forest
                30, 451235912, HauntedHouseGenerator.STARTING_POOL, "haunted_house");
    }


    @Override
    public ConfiguredStructureFeature<?, ?> getStructureFeature() {
        return TGConfiguredStructureFeatures.HAUNTED_HOUSE_STRUCTURE_CONFIG.value();
    }



    public static class HauntedHouseGenerator {
        public static final RegistryEntry<StructurePool> STARTING_POOL;
        public static final RegistryEntry<StructurePool> DOWNSTAIRS;

        public HauntedHouseGenerator() {
        }

        public static void init() {
        }

        // change from legacy single to processed single
        // if water is removed from blockentities (like chests) it might give the error message "Tried access block entity before it was created"
        static {
            STARTING_POOL = StructurePools.register(new StructurePool(new Identifier(TheGraveyard.MOD_ID, "haunted_house/start_pool"), new Identifier("empty"), ImmutableList.of(Pair.of(
                    StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":haunted_house/start_pool/haunted_house_01"), 1)), StructurePool.Projection.RIGID));
            DOWNSTAIRS = StructurePools.register(new StructurePool(new Identifier(TheGraveyard.MOD_ID, "haunted_house/downstairs_pool"), new Identifier("empty"), ImmutableList.of(Pair.of(
                    StructurePoolElement.ofProcessedSingle(TheGraveyard.MOD_ID + ":haunted_house/downstairs_pool/haunted_house_downstairs", TGProcessors.WATERLOGGED_LIST), 1)), StructurePool.Projection.RIGID));


        }
    }

}

 */
