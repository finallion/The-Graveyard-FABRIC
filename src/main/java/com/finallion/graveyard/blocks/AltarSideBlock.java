package com.finallion.graveyard.blocks;

import com.finallion.graveyard.init.TGParticles;
import com.finallion.graveyard.init.TGSounds;
import com.finallion.graveyard.util.MathUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.*;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

public class AltarSideBlock extends Block {
    public static final IntProperty FACES = IntProperty.of("faces", 1, 9);

    public AltarSideBlock(Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACES, 1)));
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
        //MathUtil.createParticleFlare(world, pos.getX(), pos.getY(), pos.getZ(), random.nextInt(300) + 150, ParticleTypes.SOUL, random);
    }


    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACES);
    }



}
