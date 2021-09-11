package com.finallion.graveyard.blocks;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.init.TGParticles;
import net.minecraft.block.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Random;

public class TGMossBlock extends MossBlock {


    public TGMossBlock(Settings settings) {
        super(settings);
    }


    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(Blocks.MOSS_BLOCK);
    }

    // graveyard fog
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);

        // can spawn
        if (TheGraveyard.config.fogSpawn(new Identifier(TheGraveyard.MOD_ID, "graveyard_fog_particle"))) {
            // how much will spawn
            if (random.nextInt(TheGraveyard.config.getParticle(new Identifier(TheGraveyard.MOD_ID, "graveyard_fog_particle")).spawnChance) == 0) {
                world.addParticle(TGParticles.GRAVEYARD_FOG_PARTICLE, (double) pos.getX() + random.nextDouble(), (double) pos.getY() + random.nextDouble(), (double) pos.getZ() + random.nextDouble(), 0.0D, 0.0D, 0.0D);
            }
        }




    }


}
