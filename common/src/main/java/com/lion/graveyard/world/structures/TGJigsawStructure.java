package com.lion.graveyard.world.structures;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.config.entries.StructureConfigEntry;
import com.lion.graveyard.init.TGStructures;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

public class TGJigsawStructure extends Structure {

    public static final MapCodec<TGJigsawStructure> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    StructureSettings.CODEC.forGetter(feature -> feature.config),
                    StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(config -> config.startPool),
                    Identifier.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(config -> config.startJigsawName),
                    Codec.intRange(0, 7).fieldOf("size").forGetter(config -> config.size),
                    HeightProvider.CODEC.fieldOf("start_height").forGetter(config -> config.startHeight),
                    Codec.BOOL.fieldOf("use_expansion_hack").forGetter(config -> config.useExpansionHack),
                    Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
                    Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter),
                    Codec.INT.fieldOf("terrain_check_size").forGetter(structure -> structure.terrainCheckSize),
                    Codec.INT.fieldOf("max_height_difference").forGetter(structure -> structure.maxHeightDifference),
                    Codec.STRING.fieldOf("structure_name").forGetter(config -> config.structureName)
            )
                    .apply(instance, TGJigsawStructure::new));


    public final Holder<StructureTemplatePool> startPool;
    public final Optional<Identifier> startJigsawName;
    public final int size;
    public final HeightProvider startHeight;
    public final boolean useExpansionHack;
    public final Optional<Heightmap.Types> projectStartToHeightmap;
    public final int maxDistanceFromCenter;
    public final int terrainCheckSize;
    public final int maxHeightDifference;
    public final String structureName;
    protected final StructureSettings config;

    public TGJigsawStructure(
            StructureSettings config,
            Holder<StructureTemplatePool> startPool,
            Optional<Identifier> startJigsawName,
            int size,
            HeightProvider startHeight,
            Boolean useExpansionHack,
            Optional<Heightmap.Types> projectStartToHeightmap,
            int maxDistanceFromCenter,
            int terrainCheckSize,
            int maxHeightDifference,
            String structureName
    ) {
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

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        if (!Graveyard.getConfig().enabled(Graveyard.id(structureName))) {
            return Optional.empty();
        }

        StructureConfigEntry entry = Graveyard.getConfig().getStructure(Graveyard.id(structureName));
        double chance = Graveyard.getConfig().structureChance(Graveyard.id(structureName));

        if (chance < 1.0D && context.random().nextDouble() >= chance) {
            return Optional.empty();
        }

        BlockPos blockpos = context.chunkPos().getMiddleBlockPosition(0);

        if (structureName.equals("crypt")) {
            int minHeight = entry.lowestY(-40);
            int maxHeight = Math.max(minHeight + 1, entry.highestY(-10));

            ChunkPos chunkPos = context.chunkPos();
            RandomSource random = context.random();

            int x = random.nextInt(chunkPos.getMaxBlockX() - chunkPos.getMinBlockX()) + chunkPos.getMinBlockX();
            int z = random.nextInt(chunkPos.getMaxBlockZ() - chunkPos.getMinBlockZ()) + chunkPos.getMinBlockZ();
            int y = random.nextInt(maxHeight - minHeight) + minHeight;
            blockpos = new BlockPos(x, y, z);

        } else if (structureName.equals("lich_prison")) {
            ChunkPos chunkPos = context.chunkPos();
            RandomSource random = context.random();

            int x = random.nextInt(chunkPos.getMaxBlockX() - chunkPos.getMinBlockX()) + chunkPos.getMinBlockX();
            int z = random.nextInt(chunkPos.getMaxBlockZ() - chunkPos.getMinBlockZ()) + chunkPos.getMinBlockZ();
            int highest = Math.max(entry.lowestY(210), entry.highestY(210));
            int y = entry.lowestY(210) + (highest > entry.lowestY(210) ? random.nextInt(highest - entry.lowestY(210) + 1) : 0);
            blockpos = new BlockPos(x, y, z);
        } else {
            if (!TGJigsawStructure.canGenerate(context, terrainCheckSize, structureName, blockpos, maxHeightDifference)) {
                return Optional.empty();
            }
        }

        return JigsawPlacement.addPieces(
                context,
                this.startPool,
                this.startJigsawName,
                this.size,
                blockpos,
                this.useExpansionHack,
                this.projectStartToHeightmap,
                new JigsawStructure.MaxDistance(this.maxDistanceFromCenter),
                PoolAliasLookup.EMPTY,
                DimensionPadding.ZERO,
                LiquidSettings.APPLY_WATERLOGGING
        );
    }


    private static boolean canGenerate(
            GenerationContext context,
            int size,
            String name,
            BlockPos centerOfChunk,
            int maxHeightDifference
    ) {
        return isTerrainFlat(context, name, centerOfChunk, size, maxHeightDifference);
    }

    protected static boolean isTerrainFlat(
            GenerationContext context,
            String name,
            BlockPos centerChunk,
            int size,
            int maxHeightDifference
    ) {
        ChunkGenerator generator = context.chunkGenerator();
        LevelHeightAccessor heightLimitView = context.heightAccessor();
        int chunkX = centerChunk.getX();
        int chunkZ = centerChunk.getZ();
        RandomState noiseConfig = context.randomState();

        int i1 = generator.getFirstOccupiedHeight(chunkX, chunkZ, Heightmap.Types.WORLD_SURFACE_WG, heightLimitView, noiseConfig);
        int j1 = generator.getFirstOccupiedHeight(chunkX, chunkZ + size, Heightmap.Types.WORLD_SURFACE_WG, heightLimitView, noiseConfig);
        int k1 = generator.getFirstOccupiedHeight(chunkX + size, chunkZ, Heightmap.Types.WORLD_SURFACE_WG, heightLimitView, noiseConfig);
        int o1 = generator.getFirstOccupiedHeight(chunkX, chunkZ - size, Heightmap.Types.WORLD_SURFACE_WG, heightLimitView, noiseConfig);
        int p1 = generator.getFirstOccupiedHeight(chunkX - size, chunkZ, Heightmap.Types.WORLD_SURFACE_WG, heightLimitView, noiseConfig);

        NoiseColumn sample1 = generator.getBaseColumn(chunkX, chunkZ, heightLimitView, noiseConfig);
        NoiseColumn sample2 = generator.getBaseColumn(chunkX, chunkZ + size, heightLimitView, noiseConfig);
        NoiseColumn sample3 = generator.getBaseColumn(chunkX + size, chunkZ, heightLimitView, noiseConfig);
        NoiseColumn sample4 = generator.getBaseColumn(chunkX, chunkZ - size, heightLimitView, noiseConfig);
        NoiseColumn sample5 = generator.getBaseColumn(chunkX - size, chunkZ, heightLimitView, noiseConfig);

        if (!name.equals("haunted_house")) {
            if (sample1.getBlock(i1).getFluidState().is(FluidTags.WATER)
                    || sample2.getBlock(j1).getFluidState().is(FluidTags.WATER)
                    || sample3.getBlock(k1).getFluidState().is(FluidTags.WATER)
                    || sample4.getBlock(o1).getFluidState().is(FluidTags.WATER)
                    || sample5.getBlock(p1).getFluidState().is(FluidTags.WATER)) {
                return false;
            }
        }

        int minSides = Math.min(Math.min(j1, p1), Math.min(o1, k1));
        int minHeight = Math.min(minSides, i1);

        int maxSides = Math.max(Math.max(j1, p1), Math.max(o1, k1));
        int maxHeight = Math.max(maxSides, i1);

        return Math.abs(maxHeight - minHeight) <= maxHeightDifference;
    }

    @Override
    public StructureType<?> type() {
        return TGStructures.TG_JIGSAW;
    }
}