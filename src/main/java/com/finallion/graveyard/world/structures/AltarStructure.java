package com.finallion.graveyard.world.structures;

/*
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
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

import java.util.Arrays;
import java.util.Collections;

public class AltarStructure extends AbstractGraveyardStructure {

    public AltarStructure(Codec<StructurePoolFeatureConfig> codec) {
        super(codec, new StructureConfigEntry(30, 24, 1093123913,
                        Arrays.asList("minecraft:snowy_plains"),
                        Collections.emptyList(), Arrays.asList("#minecraft", "#terralith"), false), // only allow in birch forests
                7, 1093123913, AltarGenerator.STARTING_POOL, "altar");
    }

    @Override
    public ConfiguredStructureFeature<?, ?> getStructureFeature() {
        return TGConfiguredStructureFeatures.MEMORIAL_TREE_STRUCTURE_CONFIG.value();
    }


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

 */
