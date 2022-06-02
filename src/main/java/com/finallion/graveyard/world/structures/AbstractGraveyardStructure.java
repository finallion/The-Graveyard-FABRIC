package com.finallion.graveyard.world.structures;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.config.StructureConfigEntry;
import com.finallion.graveyard.init.TGEntities;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.block.BlockState;
import net.minecraft.structure.*;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.tag.BiomeTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.OverworldBiomeCreator;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.structure.StructureType;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/*
public abstract class AbstractGraveyardStructure extends StructureType {
    private int seed;
    private final StructureConfigEntry config;
    private String structureName;
    private int size;

    public static final Pool<SpawnSettings.SpawnEntry> MONSTER_SPAWNS = Pool.of(
            new SpawnSettings.SpawnEntry(TGEntities.SKELETON_CREEPER, 35, 1, 1),
            new SpawnSettings.SpawnEntry(TGEntities.REVENANT, 45, 1, 3),
            new SpawnSettings.SpawnEntry(TGEntities.GHOUL, 50, 1, 3));

    public static final Pool<SpawnSettings.SpawnEntry> EMPTY = Pool.of();

    public AbstractGraveyardStructure(Codec<FeatureConfig> codec, StructureConfigEntry config, int size, int seed, RegistryEntry<StructurePool> pool, String name) {
        super(Config.CODEC, (context -> AbstractGraveyardStructure.createPiecesGenerator(context, config, size, name)), PostPlacementProcessor.EMPTY);
        this.config = config;
        this.seed = seed;
        this.structureName = name;
    }

    public StructureConfigEntry getStructureConfigEntry() {
        return config;
    }


    public abstract ConfiguredStructureFeature<?, ?> getStructureFeature();

    public String getStructureName() {
        return structureName;
    }


    private static boolean canGenerate(Context context, StructureConfigEntry config, int size, String name) {
        BlockPos centerOfChunk = context.chunkPos().getCenterAtY(0);

        if (!isCorrectBiome(context, config, name)) {
            return false;
        }

        if (!isTerrainFlat(context.chunkGenerator(), centerOfChunk.getX(), centerOfChunk.getZ(), context.world(), size)) {
            return false;
        }

        //if (!isWater(context.chunkGenerator(), centerOfChunk.getX(), centerOfChunk.getZ(), size)) {
        //    return false;
        //}

        return true;
    }

    public Optional<StructurePosition> createPiecesGenerator(Context context, StructureConfigEntry config, int size, String name) {
        BlockPos blockpos = context.chunkPos().getCenterAtY(0);

        if (!AbstractGraveyardStructure.canGenerate(context, config, size, name)) {
            return Optional.empty();
        }

        Optional<StructurePosition> structurePiecesGenerator =
                StructurePoolBasedGenerator.generate(
                        context,
                        PoolStructurePiece::new,
                        blockpos,
                        false,
                        true
                );

        return structurePiecesGenerator;
    }

    protected static boolean isCorrectBiome(Context context, StructureConfigEntry config, String name) {
        BlockPos blockpos = context.chunkPos().getCenterAtY(0);

        RegistryEntry<Biome> biome = context.chunkGenerator().getBiomeForNoiseGen(BiomeCoords.fromBlock(blockpos.getX()), BiomeCoords.fromBlock(blockpos.getY()), BiomeCoords.fromBlock(blockpos.getZ()));

        if (TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, name))
                && BiomeSelectionUtil.parseBiomes(TheGraveyard.config.structureConfigEntries.get(name).whitelist, TheGraveyard.config.structureConfigEntries.get(name).blacklist, biome)
                && BiomeSelectionUtil.parseWhitelistedMods(TheGraveyard.config.structureConfigEntries.get(name).modWhitelist, biome)) {
            return true;
        }

        return false;

    }


    protected static boolean isTerrainFlat(ChunkGenerator generator, int chunkX, int chunkZ, HeightLimitView heightLimitView, int size) {
        // center of generation is chunkX 0 chunkZ (i)
        // checks:
        //
        // n    j    l
        // o    i    k
        // q    p    m

        int i1 = generator.getHeightInGround(chunkX, chunkZ, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView);
        int j1 = generator.getHeightInGround(chunkX, chunkZ + size, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView);
        int k1 = generator.getHeightInGround(chunkX + size, chunkZ, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView);
        int o1 = generator.getHeightInGround(chunkX, chunkZ - size, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView);
        int p1 = generator.getHeightInGround(chunkX - size, chunkZ, Heightmap.Type.WORLD_SURFACE_WG, heightLimitView);

        VerticalBlockSample sample1 = generator.getColumnSample(chunkX, chunkZ, heightLimitView);
        VerticalBlockSample sample2 = generator.getColumnSample(chunkX, chunkZ + size, heightLimitView);
        VerticalBlockSample sample3 = generator.getColumnSample(chunkX + size, chunkZ, heightLimitView);
        VerticalBlockSample sample4 = generator.getColumnSample(chunkX, chunkZ - size, heightLimitView);
        VerticalBlockSample sample5 = generator.getColumnSample(chunkX - size, chunkZ, heightLimitView);

        // subtract -1 if getHeightOnGround
        if (sample1.getState(i1).getFluidState().isIn(FluidTags.WATER) || sample2.getState(j1).getFluidState().isIn(FluidTags.WATER) || sample3.getState(k1).getFluidState().isIn(FluidTags.WATER) || sample4.getState(o1).getFluidState().isIn(FluidTags.WATER) || sample5.getState(p1).getFluidState().isIn(FluidTags.WATER)) {
            return false;
        }

        int minSides = Math.min(Math.min(j1, p1), Math.min(o1, k1));
        int minHeight = Math.min(minSides, i1);

        int maxSides = Math.max(Math.max(j1, p1), Math.max(o1, k1));
        int maxHeight = Math.max(maxSides, i1);

        return Math.abs(maxHeight - minHeight) <= TheGraveyard.config.integerEntries.get("maxTerrainHeightDifference");

    }

    protected static boolean isWater(ChunkGenerator generator, int chunkX, int chunkZ, int size) {
        Set<RegistryEntry<Biome>> biomesInAreaOne = generator.getBiomeSource().getBiomesInArea(BiomeCoords.fromBlock(chunkX), 0, BiomeCoords.fromBlock(chunkZ), size, generator.getMultiNoiseSampler());

        for (RegistryEntry<Biome> biome : biomesInAreaOne) {
            if (biome.isIn(BiomeTags.IS_OCEAN) || biome.isIn(BiomeTags.IS_DEEP_OCEAN) || biome.isIn(BiomeTags.IS_RIVER)) {
                return false;
            }
        }

        return true;

    }

}
*/
