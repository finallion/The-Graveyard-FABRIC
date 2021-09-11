package com.finallion.graveyard.blocks;

import com.finallion.graveyard.init.TGParticles;
import net.minecraft.block.*;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Random;
import java.util.function.Supplier;

public class TGGrassBlock extends GrassBlock {


    public TGGrassBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(Blocks.GRASS_BLOCK);
    }


}
