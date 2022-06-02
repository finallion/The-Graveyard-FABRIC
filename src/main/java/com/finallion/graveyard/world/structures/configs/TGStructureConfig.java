package com.finallion.graveyard.world.structures.configs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.structure.pool.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.feature.FeatureConfig;
import java.util.ArrayList;
import java.util.List;

/*
public class TGStructureConfig implements FeatureConfig {

    public static final Codec<TGStructureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            StructurePool.REGISTRY_CODEC.fieldOf("start_pool").forGetter(config -> config.startPool),
            Codec.intRange(0, 7).fieldOf("pool_size").forGetter(config -> config.poolSize),
            Codec.intRange(0, 100).fieldOf("terrain_check").forGetter(config -> config.terrainCheck),
            Codec.intRange(0, 100).fieldOf("structure_avoid_radius").forGetter(config -> config.structureAvoidRadius),
            Identifier.CODEC.listOf().fieldOf("whitelist").orElse(new ArrayList<>()).forGetter(config -> config.whitelist),
            Identifier.CODEC.listOf().fieldOf("blacklist").orElse(new ArrayList<>()).forGetter(config -> config.blacklist),
            Identifier.CODEC.listOf().fieldOf("mod_whitelist").orElse(new ArrayList<>()).forGetter(config -> config.modWhitelist)
            ).apply(instance, TGStructureConfig::new));


    public final RegistryEntry<StructurePool> startPool;
    public final int poolSize;
    public final int terrainCheck;
    public final int structureAvoidRadius;
    public final List<Identifier> whitelist;
    public final List<Identifier> blacklist;
    public final List<Identifier> modWhitelist;

    public TGStructureConfig(RegistryEntry<StructurePool> startPool, int poolSize, int terrainCheck,
                             int structureAvoidRadius, List<Identifier> whitelist, List<Identifier> blacklist, List<Identifier> modWhitelist
    ) {

        this.startPool = startPool;
        this.poolSize = poolSize;
        this.terrainCheck = terrainCheck;
        this.structureAvoidRadius = structureAvoidRadius;
        this.whitelist = whitelist;
        this.blacklist = blacklist;
        this.modWhitelist = modWhitelist;
    }

}

 */
