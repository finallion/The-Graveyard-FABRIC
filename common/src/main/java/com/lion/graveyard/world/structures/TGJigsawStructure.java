package com.lion.graveyard.world.structures;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.init.TGStructures;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

import java.util.Optional;

public class TGJigsawStructure extends Structure {

    public static final Codec<TGJigsawStructure> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Config.CODEC.forGetter(feature -> feature.config),
                    StructurePool.REGISTRY_CODEC.fieldOf("start_pool").forGetter(config -> config.startPool),
                    Identifier.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(config -> config.startJigsawName),
                    Codec.intRange(0, 7).fieldOf("size").forGetter(config -> config.size),
                    HeightProvider.CODEC.fieldOf("start_height").forGetter(config -> config.startHeight),
                    Codec.BOOL.fieldOf("use_expansion_hack").forGetter(config -> config.useExpansionHack),
                    Heightmap.Type.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
                    Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter),
                    Codec.INT.fieldOf("terrain_check_size").forGetter(structure -> structure.terrainCheckSize),
                    Codec.INT.fieldOf("max_height_difference").forGetter(structure -> structure.maxHeightDifference),
                    Codec.STRING.fieldOf("structure_name").forGetter(config -> config.structureName))
                    .apply(instance, TGJigsawStructure::new));


    public final RegistryEntry<StructurePool> startPool;
    public final Optional<Identifier> startJigsawName;
    public final int size;
    public final HeightProvider startHeight;
    public final boolean useExpansionHack;
    public final Optional<Heightmap.Type> projectStartToHeightmap;
    public final int maxDistanceFromCenter;
    public final int terrainCheckSize;
    public final int maxHeightDifference;
    public final String structureName;
    protected final Config config;

    public TGJigsawStructure(Config config, RegistryEntry<StructurePool> startPool, Optional<Identifier> startJigsawName, int size, HeightProvider startHeight, Boolean useExpansionHack, Optional<Heightmap.Type> projectStartToHeightmap, int maxDistanceFromCenter, int terrainCheckSize, int maxHeightDifference, /* List<String> whitelist, List<String> blacklist,*/ String structureName) {
        super(config);
        this.config = config;
        this.startPool = startPool;
        this.startJigsawName = startJigsawName;
        this.size = size;
        this.startHeight = startHeight;
        this.useExpansionHack = useExpansionHack;
        this.projectStartToHeightmap = projectStartToHeightmap;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
        this.maxHeightDifference = maxHeightDifference;
        this.terrainCheckSize = terrainCheckSize;
        this.structureName = structureName;
    }

    public Optional<StructurePosition> getStructurePosition(Context context) {
        if (!Graveyard.getConfig().enabled(new Identifier(Graveyard.MOD_ID, structureName))) {
            return Optional.empty();
        }

        BlockPos blockpos = context.chunkPos().getCenterAtY(0);

        if (structureName.equals("crypt")) {
            int minHeight = -40; // default: -40
            int maxHeight = -10; // default: -10

            ChunkPos chunkPos = context.chunkPos();
            Random random = context.random();

            int x = random.nextInt(chunkPos.getEndX() - chunkPos.getStartX()) + chunkPos.getStartX();
            int z = random.nextInt(chunkPos.getEndZ() - chunkPos.getStartZ()) + chunkPos.getStartZ();
            int y = random.nextInt(maxHeight - minHeight) + minHeight;
            blockpos = new BlockPos(x, y, z);

        } else if (structureName.equals("lich_prison")) {
            ChunkPos chunkPos = context.chunkPos();
            Random random = context.random();

            int x = random.nextInt(chunkPos.getEndX() - chunkPos.getStartX()) + chunkPos.getStartX();
            int z = random.nextInt(chunkPos.getEndZ() - chunkPos.getStartZ()) + chunkPos.getStartZ();
            int y = 210;
            blockpos = new BlockPos(x, y, z);
        } else {
            if (!TGJigsawStructure.canGenerate(context, terrainCheckSize, structureName, blockpos, maxHeightDifference)) {
                return Optional.empty();
            }
        }

        return StructurePoolBasedGenerator.generate(
                context,
                this.startPool,
                this.startJigsawName,
                this.size, blockpos,
                this.useExpansionHack,
                this.projectStartToHeightmap,
                this.maxDistanceFromCenter,
                StructurePoolAliasLookup.EMPTY);
    }


    private static boolean canGenerate(Context context, int size, String name, BlockPos centerOfChunk, int maxHeightDifference) {
        return isTerrainFlat(context, name, centerOfChunk, size, maxHeightDifference);
    }

    protected static boolean isTerrainFlat(Context context, String name, BlockPos centerChunk, int size, int maxHeightDifference) {
        ChunkGenerator generator = context.chunkGenerator();
        HeightLimitView heightLimitView = context.world();
        int chunkX = centerChunk.getX();
        int chunkZ = centerChunk.getZ();
        NoiseConfig noiseConfig = context.noiseConfig();

        int i1 = generator.getHeightInGround(chunkX, chunkZ, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView, noiseConfig);
        int j1 = generator.getHeightInGround(chunkX, chunkZ + size, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView, noiseConfig);
        int k1 = generator.getHeightInGround(chunkX + size, chunkZ, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView, noiseConfig);
        int o1 = generator.getHeightInGround(chunkX, chunkZ - size, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView, noiseConfig);
        int p1 = generator.getHeightInGround(chunkX - size, chunkZ, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView, noiseConfig);

        VerticalBlockSample sample1 = generator.getColumnSample(chunkX, chunkZ, heightLimitView, noiseConfig);
        VerticalBlockSample sample2 = generator.getColumnSample(chunkX, chunkZ + size, heightLimitView, noiseConfig);
        VerticalBlockSample sample3 = generator.getColumnSample(chunkX + size, chunkZ, heightLimitView, noiseConfig);
        VerticalBlockSample sample4 = generator.getColumnSample(chunkX, chunkZ - size, heightLimitView, noiseConfig);
        VerticalBlockSample sample5 = generator.getColumnSample(chunkX - size, chunkZ, heightLimitView, noiseConfig);

        // subtract -1 if getHeightOnGround
        if (!name.equals("haunted_house")) {
            if (sample1.getState(i1).getFluidState().isIn(FluidTags.WATER) || sample2.getState(j1).getFluidState().isIn(FluidTags.WATER) || sample3.getState(k1).getFluidState().isIn(FluidTags.WATER) || sample4.getState(o1).getFluidState().isIn(FluidTags.WATER) || sample5.getState(p1).getFluidState().isIn(FluidTags.WATER)) {
                return false;
            }
        }

        int minSides = Math.min(Math.min(j1, p1), Math.min(o1, k1));
        int minHeight = Math.min(minSides, i1);

        int maxSides = Math.max(Math.max(j1, p1), Math.max(o1, k1));
        int maxHeight = Math.max(maxSides, i1);

        return Math.abs(maxHeight - minHeight) <= maxHeightDifference;
    }

    public StructureType<?> getType() {
        return TGStructures.TG_JIGSAW;
    }
}