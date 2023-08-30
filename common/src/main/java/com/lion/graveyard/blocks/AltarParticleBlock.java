package com.lion.graveyard.blocks;

import com.lion.graveyard.init.TGParticles;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class AltarParticleBlock extends Block {

    public AltarParticleBlock(Settings settings) {
        super(settings);
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);

        if (random.nextInt(10) == 0) {
            world.addParticle(TGParticles.GRAVEYARD_SOUL_PARTICLE, (double)pos.getX() + random.nextDouble(), (double)pos.getY() + 1.3D, (double)pos.getZ() + random.nextDouble(), 0.0D, 0.01D, 0.0D);
        }

        if (random.nextInt(100) == 0) {
            world.addParticle(TGParticles.GRAVEYARD_HAND_PARTICLE, (double)pos.getX() + random.nextDouble(), (double)pos.getY() + 1.2D, (double)pos.getZ() + random.nextDouble(), 0.0D, 0.0D, 0.0D);
        }

        if (random.nextInt(100) == 0) {
            world.addParticle(TGParticles.GRAVEYARD_LEFT_HAND_PARTICLE, (double)pos.getX() + random.nextDouble(), (double)pos.getY() + 1.2D, (double)pos.getZ() + random.nextDouble(), 0.0D, 0.0D, 0.0D);
        }
    }
}
