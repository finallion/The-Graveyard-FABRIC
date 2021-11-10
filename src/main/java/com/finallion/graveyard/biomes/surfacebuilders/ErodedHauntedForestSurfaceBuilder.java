package com.finallion.graveyard.biomes.surfacebuilders;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

import java.util.Random;

public class ErodedHauntedForestSurfaceBuilder extends SurfaceBuilder<TernarySurfaceConfig> {

    public ErodedHauntedForestSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
        super(codec);
    }

    @Override
    public void generate(Random random, Chunk chunk, Biome biome, int x, int z, int height, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, int i, long l, TernarySurfaceConfig surfaceConfig) {

        if (noise > 1.75D) {
            SurfaceBuilder.DEFAULT.generate(random, chunk, biome, x, z, height, noise, defaultBlock, defaultFluid, seaLevel, i, l, SurfaceBuilder.COARSE_DIRT_CONFIG);
        } else if (noise > -0.95D) {
            if (random.nextBoolean()) {
                SurfaceBuilder.DEFAULT.generate(random, chunk, biome, x, z, height, noise, defaultBlock, defaultFluid, seaLevel, i, l, TGSurfaceBuilders.Configs.SOUL_SOIL_CONFIG);
            } else {
                SurfaceBuilder.DEFAULT.generate(random, chunk, biome, x, z, height, noise, defaultBlock, defaultFluid, seaLevel, i, l, TGSurfaceBuilders.Configs.SOUL_SAND_CONFIG);
            }
        } else {
            if (random.nextInt(4) == 0) {
                SurfaceBuilder.DEFAULT.generate(random, chunk, biome, x, z, height, noise, defaultBlock, defaultFluid, seaLevel, i, l, TGSurfaceBuilders.Configs.PARTICLE_MOSS_CONFIG);
            } else {
                SurfaceBuilder.DEFAULT.generate(random, chunk, biome, x, z, height, noise, defaultBlock, defaultFluid, seaLevel, i, l, TGSurfaceBuilders.Configs.MOSS_CONFIG);
            }

        }
    }



}
