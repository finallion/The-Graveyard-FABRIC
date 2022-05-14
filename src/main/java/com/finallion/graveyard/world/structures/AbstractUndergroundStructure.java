package com.finallion.graveyard.world.structures;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.config.StructureConfigEntry;
import com.finallion.graveyard.init.TGEntities;
import com.finallion.graveyard.util.BiomeSelectionUtil;
import com.mojang.serialization.Codec;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.PostPlacementProcessor;
import net.minecraft.structure.StructureGeneratorFactory;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.tag.BiomeTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

public abstract class AbstractUndergroundStructure extends StructureFeature<StructurePoolFeatureConfig> {
    private final StructureConfigEntry config;
    private String structureName;

    public AbstractUndergroundStructure(StructureConfigEntry config, String name) {
        super(StructurePoolFeatureConfig.CODEC, (context -> AbstractUndergroundStructure.createPiecesGenerator(context, config, name)), PostPlacementProcessor.EMPTY);
        this.config = config;
        this.structureName = name;
    }

    public StructureConfigEntry getStructureConfigEntry() {
        return config;
    }


    public abstract ConfiguredStructureFeature<?, ?> getStructureFeature();

    public String getStructureName() {
        return structureName;
    }


    private static boolean canGenerate(StructureGeneratorFactory.Context<StructurePoolFeatureConfig> context, StructureConfigEntry config, String name) {
        if (!isCorrectBiome(context, config, name)) {
            return false;
        }

        return true;
    }

    public static Optional<StructurePiecesGenerator<StructurePoolFeatureConfig>> createPiecesGenerator(StructureGeneratorFactory.Context<StructurePoolFeatureConfig> context, StructureConfigEntry config, String name) {
        int minHeight = -40; // default: -40
        int maxHeight = -10; // default: -10

        ChunkPos chunkPos = context.chunkPos();
        Random random = new Random();

        if (!AbstractUndergroundStructure.canGenerate(context, config, name)) {
            return Optional.empty();
        }

        int x = random.nextInt(chunkPos.getEndX() - chunkPos.getStartX()) + chunkPos.getStartX();
        int z = random.nextInt(chunkPos.getEndZ() - chunkPos.getStartZ()) + chunkPos.getStartZ();
        int y = random.nextInt(maxHeight - minHeight) + minHeight;

        Optional<StructurePiecesGenerator<StructurePoolFeatureConfig>> structurePiecesGenerator =
                StructurePoolBasedGenerator.generate(
                        context,
                        PoolStructurePiece::new,
                        new BlockPos(x, y, z),
                        false,
                        false
                );

        return structurePiecesGenerator;
    }

    protected static boolean isCorrectBiome(StructureGeneratorFactory.Context<StructurePoolFeatureConfig> context, StructureConfigEntry config, String name) {
        BlockPos blockpos = context.chunkPos().getCenterAtY(0);

        RegistryEntry<Biome> biome = context.chunkGenerator().getBiomeForNoiseGen(BiomeCoords.fromBlock(blockpos.getX()), BiomeCoords.fromBlock(blockpos.getY()), BiomeCoords.fromBlock(blockpos.getZ()));

        if (TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, name))
                && BiomeSelectionUtil.parseBiomes(TheGraveyard.config.structureConfigEntries.get(name).whitelist, TheGraveyard.config.structureConfigEntries.get(name).blacklist, biome)
                && BiomeSelectionUtil.parseWhitelistedMods(TheGraveyard.config.structureConfigEntries.get(name).modWhitelist, biome)) {
            return true;
        }

        return false;

    }

}
