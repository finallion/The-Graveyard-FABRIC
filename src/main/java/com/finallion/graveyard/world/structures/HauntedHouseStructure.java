package com.finallion.graveyard.world.structures;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.config.StructureConfigEntry;
import com.finallion.graveyard.init.TGProcessors;
import com.finallion.graveyard.init.TGStructures;
import com.finallion.graveyard.world.processors.RemoveWaterloggedProcessor;
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
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.JigsawFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

import java.util.Arrays;
import java.util.Optional;

public class HauntedHouseStructure extends AbstractGraveyardStructure {

    public HauntedHouseStructure(Codec<StructurePoolFeatureConfig> codec) {
        super(codec, new StructureConfigEntry(25, 20, 451235912,
                        Arrays.asList(Biome.Category.FOREST.getName(), Biome.Category.SWAMP.getName()),
                        Arrays.asList("forest", "flower_forest", "birch_forest", "old_growth_birch_forest", "windswept_forest")), // only allow in swamp and dark forest
                30, 451235912, HauntedHouseGenerator.STARTING_POOL, "haunted_house");
    }


    @Override
    public ConfiguredStructureFeature<?, ?> getStructureFeature() {
        return TGStructures.HAUNTED_HOUSE_STRUCTURE_CONFIG;
    }


    public static class HauntedHouseGenerator {
        public static final StructurePool STARTING_POOL;
        public static final StructurePool DOWNSTAIRS;

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
