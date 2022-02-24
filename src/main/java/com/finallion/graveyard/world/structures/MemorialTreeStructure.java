package com.finallion.graveyard.world.structures;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.config.StructureConfigEntry;
import com.finallion.graveyard.init.TGStructures;
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
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.JigsawFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

import java.util.Arrays;
import java.util.Optional;

public class MemorialTreeStructure extends AbstractGraveyardStructure {

    public MemorialTreeStructure(Codec<StructurePoolFeatureConfig> codec) {
        super(codec, new StructureConfigEntry(14, 12, 529239621,
                        Arrays.asList("#" + Biome.Category.FOREST.getName()),
                        Arrays.asList("minecraft:forest", "minecraft:flower_forest", "minecraft:dark_forest", "minecraft:windswept_forest", "graveyard:haunted_lakes", "graveyard:haunted_forest", "graveyard:eroded_haunted_forest")), // only allow in birch forests
                10, 529239621, MemorialTreeGenerator.STARTING_POOL, "memorial_tree");
    }

    @Override
    public ConfiguredStructureFeature<?, ?> getStructureFeature() {
        return TGStructures.MEMORIAL_TREE_STRUCTURE_CONFIG;
    }


    public static class MemorialTreeGenerator {
        public static final StructurePool STARTING_POOL;

        public MemorialTreeGenerator() {
        }

        public static void init() {
        }

        static {
            STARTING_POOL = StructurePools.register(new StructurePool(new Identifier(TheGraveyard.MOD_ID, "memorial_tree"), new Identifier("empty"), ImmutableList.of(Pair.of(StructurePoolElement.ofLegacySingle(TheGraveyard.MOD_ID + ":memorial_tree/memorial_tree_01"), 1)), StructurePool.Projection.RIGID));
        }
    }

}
