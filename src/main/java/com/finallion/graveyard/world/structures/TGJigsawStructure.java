package com.finallion.graveyard.world.structures;

import com.finallion.graveyard.init.TGStructureType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.heightprovider.ConstantHeightProvider;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.structure.JigsawStructure;
import net.minecraft.world.gen.structure.StructureType;

import java.util.Optional;
import java.util.function.Function;

public class TGJigsawStructure extends StructureType {
    public static final int MAX_SIZE = 128;

    public static final Codec<TGJigsawStructure> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    StructureType.Config.CODEC.forGetter(feature -> feature.config),
                    StructurePool.REGISTRY_CODEC.fieldOf("start_pool").forGetter(config -> config.startPool),
                    Identifier.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(config -> config.startJigsawName),
                    Codec.intRange(0, 7).fieldOf("size").forGetter(config -> config.size),
                    HeightProvider.CODEC.fieldOf("start_height").forGetter(config -> config.startHeight),
                    Codec.BOOL.fieldOf("use_expansion_hack").forGetter(config -> config.useExpansionHack),
                    Heightmap.Type.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
                    Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter))
                    .apply(instance, TGJigsawStructure::new));


    public final RegistryEntry<StructurePool> startPool;
    public final Optional<Identifier> startJigsawName;
    public final int size;
    public final HeightProvider startHeight;
    public final boolean useExpansionHack;
    public final Optional<Heightmap.Type> projectStartToHeightmap;
    public final int maxDistanceFromCenter;
    protected final StructureType.Config config;



    private static Function<TGJigsawStructure, DataResult<TGJigsawStructure>> createValidator() {
        return (feature) -> {
            byte var10000;
            switch (feature.getTerrainAdaptation()) {
                case NONE:
                    var10000 = 0;
                    break;
                case BURY:
                case BEARD_THIN:
                case BEARD_BOX:
                    var10000 = 12;
                    break;
                default:
                    throw new IncompatibleClassChangeError();
            }

            int i = var10000;
            return feature.maxDistanceFromCenter + i > 128 ? DataResult.error("Structure size including terrain adaptation must not exceed 128") : DataResult.success(feature);
        };
    }

    public TGJigsawStructure(Config config, RegistryEntry<StructurePool> startPool, Optional<Identifier> startJigsawName, int size, HeightProvider startHeight, Boolean useExpansionHack, Optional<Heightmap.Type> projectStartToHeightmap, int maxDistanceFromCenter) {
        super(config);
        this.config = config;
        this.startPool = startPool;
        this.startJigsawName = startJigsawName;
        this.size = size;
        this.startHeight = startHeight;
        this.useExpansionHack = useExpansionHack;
        this.projectStartToHeightmap = projectStartToHeightmap;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
    }

    public TGJigsawStructure(Config config, RegistryEntry<StructurePool> startPool, int size, HeightProvider startHeight, boolean useExpansionHack, Heightmap.Type projectStartToHeightmap) {
        this(config, startPool, Optional.empty(), size, startHeight, useExpansionHack, Optional.of(projectStartToHeightmap), 80);
    }

    public TGJigsawStructure(Config config, RegistryEntry<StructurePool> startPool, int size, HeightProvider startHeight, boolean useExpansionHack) {
        this(config, startPool, Optional.empty(), size, startHeight, useExpansionHack, Optional.empty(), 80);
    }

    public Optional<StructurePosition> getStructurePosition(Context context) {
        ChunkPos chunkPos = context.chunkPos();
        int i = this.startHeight.get(context.random(), new HeightContext(context.chunkGenerator(), context.world()));
        BlockPos blockPos = new BlockPos(chunkPos.getStartX(), i, chunkPos.getStartZ());
        StructurePools.method_44111();
        return StructurePoolBasedGenerator.generate(context, this.startPool, this.startJigsawName, this.size, blockPos, this.useExpansionHack, this.projectStartToHeightmap, this.maxDistanceFromCenter);
    }

    public net.minecraft.structure.StructureType<?> getType() {
        return TGStructureType.TG_JIGSAW;
    }
}