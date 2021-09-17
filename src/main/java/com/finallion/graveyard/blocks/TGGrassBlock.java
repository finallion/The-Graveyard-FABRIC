package com.finallion.graveyard.blocks;

import com.finallion.graveyard.init.TGParticles;
import net.minecraft.block.*;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
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

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for(int l = 0; l < 14; ++l) {
            mutable.set(i + MathHelper.nextInt(random, -10, 10), j - random.nextInt(10), k + MathHelper.nextInt(random, -10, 10));
            BlockState blockState = world.getBlockState(mutable);
            if (!blockState.isFullCube(world, mutable)) {
                world.addParticle(ParticleTypes.SPORE_BLOSSOM_AIR, (double)mutable.getX() + random.nextDouble(), (double)mutable.getY() + random.nextDouble(), (double)mutable.getZ() + random.nextDouble(), 0.0D, 0.0D, 0.0D);
            }
        }

    }


}
