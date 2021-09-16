package com.finallion.graveyard.blocks;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.init.TGParticles;
import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FlowerFeature;

import java.util.List;
import java.util.Random;

public class TGMossBlock extends Block implements Fertilizable {


    public TGMossBlock(Settings settings) {
        super(settings);
    }


    // graveyard fog
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);

        // can spawn
        if (TheGraveyard.config.fogParticle.canGenerate) {
            // how much will spawn
            if (random.nextInt(TheGraveyard.config.fogParticle.fog_particle_spawn) == 0) {
                world.addParticle(TGParticles.GRAVEYARD_FOG_PARTICLE, (double) pos.getX() + random.nextDouble(), (double) pos.getY() + random.nextDouble(), (double) pos.getZ() + random.nextDouble(), 0.0D, 0.0D, 0.0D);
            }
        }




    }

    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        return world.getBlockState(pos.up()).isAir();
    }

    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        BlockPos blockPos = pos.up();
        BlockState blockState = Blocks.GRASS.getDefaultState();

        label48:
        for (int i = 0; i < 128; ++i) {
            BlockPos blockPos2 = blockPos;

            for (int j = 0; j < i / 16; ++j) {
                blockPos2 = blockPos2.add(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
                if (!world.getBlockState(blockPos2.down()).isOf(this) || world.getBlockState(blockPos2).isFullCube(world, blockPos2)) {
                    continue label48;
                }
            }

            BlockState blockState2 = world.getBlockState(blockPos2);
            if (blockState2.isOf(blockState.getBlock()) && random.nextInt(10) == 0) {
                ((Fertilizable) blockState.getBlock()).grow(world, random, blockPos2, blockState2);
            }

            if (blockState2.isAir()) {
                BlockState blockState4;
                if (random.nextInt(8) == 0) {
                    List<ConfiguredFeature<?, ?>> list = world.getBiome(blockPos2).getGenerationSettings().getFlowerFeatures();
                    if (list.isEmpty()) {
                        continue;
                    }

                    ConfiguredFeature<?, ?> configuredFeature = (ConfiguredFeature) list.get(0);
                    FlowerFeature flowerFeature = (FlowerFeature) configuredFeature.feature;
                    blockState4 = flowerFeature.getFlowerState(random, blockPos2, configuredFeature.getConfig());
                } else {
                    blockState4 = blockState;
                }

                if (blockState4.canPlaceAt(world, blockPos2)) {
                    world.setBlockState(blockPos2, blockState4, 3);
                }
            }
        }
    }


}
