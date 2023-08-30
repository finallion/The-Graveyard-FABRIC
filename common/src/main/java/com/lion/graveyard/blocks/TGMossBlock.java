package com.lion.graveyard.blocks;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.init.TGParticles;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MossBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

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
        if (Graveyard.getConfig().fogSpawn(new Identifier(Graveyard.MOD_ID, "graveyard_fog_particle"))) {
            // how much will spawn
            if (random.nextInt(Graveyard.getConfig().getParticle(new Identifier(Graveyard.MOD_ID, "graveyard_fog_particle")).spawnChance) == 0) {
                world.addParticle(TGParticles.GRAVEYARD_FOG_PARTICLE, (double) pos.getX() + random.nextDouble(), (double) pos.getY() + random.nextDouble(), (double) pos.getZ() + random.nextDouble(), 0.0D, 0.0D, 0.0D);
            }

            if (random.nextInt(250) == 0) {
                world.addParticle(TGParticles.GRAVEYARD_HAND_PARTICLE, (double)pos.getX() + random.nextDouble(), (double)pos.getY() + 1.2D, (double)pos.getZ() + random.nextDouble(), 0.0D, 0.0D, 0.0D);
            }
        }
    }

}
