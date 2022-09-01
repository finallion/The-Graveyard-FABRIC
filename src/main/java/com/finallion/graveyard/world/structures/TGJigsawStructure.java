package com.finallion.graveyard.world.structures;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.init.TGEntities;
import com.finallion.graveyard.init.TGStructureType;
import com.finallion.graveyard.util.BiomeSelectionUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TGJigsawStructure extends Structure {
    public static final int MAX_SIZE = 128;

    public static final Pool<SpawnSettings.SpawnEntry> MONSTER_SPAWNS = Pool.of(
            new SpawnSettings.SpawnEntry(TGEntities.SKELETON_CREEPER, 35, 1, 1),
            new SpawnSettings.SpawnEntry(TGEntities.REVENANT, 45, 1, 3),
            new SpawnSettings.SpawnEntry(TGEntities.GHOUL, 50, 1, 3));
    public static final Pool<SpawnSettings.SpawnEntry> EMPTY = Pool.of();
    public static final Pool<SpawnSettings.SpawnEntry> ILLAGER_SPAWNS = Pool.of(
            new SpawnSettings.SpawnEntry(EntityType.PILLAGER, 10, 1, 1),
            new SpawnSettings.SpawnEntry(EntityType.VINDICATOR, 1, 1, 1));


    //StructureConfigEntry config
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
                    Codec.STRING.listOf().fieldOf("whitelist").orElse(new ArrayList<>()).forGetter(config -> config.whitelist),
                    Codec.STRING.listOf().fieldOf("mod_whitelist").orElse(new ArrayList<>()).forGetter(config -> config.modWhitelist),
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
    public final List<String> whitelist;
    public final List<String> modWhitelist;
    public final String structureName;
    protected final Config config;

    public TGJigsawStructure(Config config, RegistryEntry<StructurePool> startPool, Optional<Identifier> startJigsawName, int size, HeightProvider startHeight, Boolean useExpansionHack, Optional<Heightmap.Type> projectStartToHeightmap, int maxDistanceFromCenter, int terrainCheckSize, int maxHeightDifference, List<String> whitelist, List<String> modWhitelist, String structureName) {
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
        this.whitelist = whitelist;
        this.modWhitelist = modWhitelist;
        this.structureName = structureName;
    }

    public TGJigsawStructure(Config config, RegistryEntry<StructurePool> startPool, int size, HeightProvider startHeight, boolean useExpansionHack, Heightmap.Type projectStartToHeightmap, int terrainCheckSize, int maxHeightDifference, List<String> whitelist, List<String> modWhitelist, String structureName) {
        this(config, startPool, Optional.empty(), size, startHeight, useExpansionHack, Optional.of(projectStartToHeightmap), 80, terrainCheckSize, maxHeightDifference, whitelist, modWhitelist, structureName);
    }

    public TGJigsawStructure(Config config, RegistryEntry<StructurePool> startPool, int size, HeightProvider startHeight, boolean useExpansionHack, int terrainCheckSize, int maxHeightDifference, List<String> whitelist, List<String> modWhitelist, String structureName) {
        this(config, startPool, Optional.empty(), size, startHeight, useExpansionHack, Optional.empty(), 80, terrainCheckSize, maxHeightDifference, whitelist, modWhitelist, structureName);
    }


    public Optional<StructurePosition> getStructurePosition(Context context) {
        if (!TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, structureName))) {
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

            if (!TGJigsawStructure.canGenerateUnderground(context, structureName)) {
                return Optional.empty();
            }
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
                this.maxDistanceFromCenter);
    }

    private static boolean canGenerateUnderground(Context context, String name) {
        if (!isCorrectBiome(context, name)) {
            return false;
        }

        return true;
    }

    private static boolean canGenerate(Context context, int size, String name, BlockPos centerOfChunk, int maxHeightDifference) {
        if (!isCorrectBiome(context, name)) {
            return false;
        }

        if (!isTerrainFlat(context, centerOfChunk, size / 2, maxHeightDifference)) {
            return false;
        }

        if (!isTerrainFlat(context, centerOfChunk, size, maxHeightDifference)) {
            return false;
        }

        return true;
    }

    protected static boolean isCorrectBiome(Context context, String name) {
        BlockPos blockpos = context.chunkPos().getCenterAtY(0);

        RegistryEntry<Biome> biome = context.chunkGenerator().getBiomeSource().getBiome(
                BiomeCoords.fromBlock(blockpos.getX()),
                BiomeCoords.fromBlock(blockpos.getY()),
                BiomeCoords.fromBlock(blockpos.getZ()), context.noiseConfig().getMultiNoiseSampler());

        if (BiomeSelectionUtil.parseBiomes(TheGraveyard.config.structureConfigEntries.get(name).biomeWhitelist, TheGraveyard.config.structureConfigEntries.get(name).biomeBlacklist, biome)) {
            return true;
        }

        return false;
    }

    protected static boolean isTerrainFlat(Context context, BlockPos centerChunk, int size, int maxHeightDifference) {
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
        //int l1 = generator.getHeightInGround(chunkX + size, chunkZ + size, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView, noiseConfig);
        //int m1 = generator.getHeightInGround(chunkX - size, chunkZ - size, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView, noiseConfig);
        //int n1 = generator.getHeightInGround(chunkX + size, chunkZ - size, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView, noiseConfig);
        //int q1 = generator.getHeightInGround(chunkX - size, chunkZ + size, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView, noiseConfig);

        VerticalBlockSample sample1 = generator.getColumnSample(chunkX, chunkZ, heightLimitView, noiseConfig);
        VerticalBlockSample sample2 = generator.getColumnSample(chunkX, chunkZ + size, heightLimitView, noiseConfig);
        VerticalBlockSample sample3 = generator.getColumnSample(chunkX + size, chunkZ, heightLimitView, noiseConfig);
        VerticalBlockSample sample4 = generator.getColumnSample(chunkX, chunkZ - size, heightLimitView, noiseConfig);
        VerticalBlockSample sample5 = generator.getColumnSample(chunkX - size, chunkZ, heightLimitView, noiseConfig);
        //VerticalBlockSample sample6 = generator.getColumnSample(chunkX + size, chunkZ + size, heightLimitView, noiseConfig);
        //VerticalBlockSample sample7 = generator.getColumnSample(chunkX - size, chunkZ - size, heightLimitView, noiseConfig);
        //VerticalBlockSample sample8 = generator.getColumnSample(chunkX + size, chunkZ - size, heightLimitView, noiseConfig);
        //VerticalBlockSample sample9 = generator.getColumnSample(chunkX - size, chunkZ + size, heightLimitView, noiseConfig);

        // subtract -1 if getHeightOnGround
        if (sample1.getState(i1).getFluidState().isIn(FluidTags.WATER) || sample2.getState(j1).getFluidState().isIn(FluidTags.WATER) || sample3.getState(k1).getFluidState().isIn(FluidTags.WATER) || sample4.getState(o1).getFluidState().isIn(FluidTags.WATER) || sample5.getState(p1).getFluidState().isIn(FluidTags.WATER)) {
            return false;
        }

        /*
        if (sample1.getState(i1).getFluidState().isIn(FluidTags.WATER) ||
                sample2.getState(j1).getFluidState().isIn(FluidTags.WATER) ||
                sample3.getState(k1).getFluidState().isIn(FluidTags.WATER) ||
                sample4.getState(o1).getFluidState().isIn(FluidTags.WATER) ||
                sample5.getState(p1).getFluidState().isIn(FluidTags.WATER) ||
                sample6.getState(l1).getFluidState().isIn(FluidTags.WATER) ||
                sample7.getState(m1).getFluidState().isIn(FluidTags.WATER) ||
                sample8.getState(n1).getFluidState().isIn(FluidTags.WATER) ||
                sample9.getState(q1).getFluidState().isIn(FluidTags.WATER)) {
            return false;
        }


        int minCorners = Math.min(Math.min(n1, m1), Math.min(q1, l1));
        int minSides = Math.min(Math.min(j1, p1), Math.min(o1, k1));
        int minHeight = Math.min(Math.min(minCorners, minSides), i1);

        int maxCorners = Math.max(Math.max(n1, m1), Math.max(q1, l1));
        int maxSides = Math.max(Math.max(j1, p1), Math.max(o1, k1));
        int maxHeight = Math.max(Math.max(maxCorners, maxSides), i1);

         */

        int minSides = Math.min(Math.min(j1, p1), Math.min(o1, k1));
        int minHeight = Math.min(minSides, i1);

        int maxSides = Math.max(Math.max(j1, p1), Math.max(o1, k1));
        int maxHeight = Math.max(maxSides, i1);

        /*
        System.out.println("Testing at..." + chunkX + " " + chunkZ + ": " + i1);
        System.out.println("Testing at..." + chunkX + " " + (chunkZ + size) + ": " + j1);
        System.out.println("Testing at..." + (chunkX + size) + " " + chunkZ + ": " + k1);
        System.out.println("Testing at..." + chunkX + " " + (chunkZ - size) + ": " + o1);
        System.out.println("Testing at..." + (chunkX - size) + " " + chunkZ + ": " + p1);
        System.out.println("Max: " + Math.abs(maxHeight - minHeight));
        System.out.println("Config: " + maxHeightDifference);
         */

        return Math.abs(maxHeight - minHeight) <= maxHeightDifference;

    }

    public StructureType<?> getType() {
        return TGStructureType.TG_JIGSAW;
    }

}