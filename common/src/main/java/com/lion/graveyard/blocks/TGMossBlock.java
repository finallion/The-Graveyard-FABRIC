package com.lion.graveyard.blocks;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.init.TGParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MossBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class TGMossBlock extends MossBlock {

    public TGMossBlock(BlockBehaviour.Properties settings) {
        super(settings);
    }

    public ItemStack getCloneItemStack(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
        return new ItemStack(Blocks.MOSS_BLOCK);
    }

    // graveyard fog
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        super.animateTick(state, world, pos, random);

        // can spawn
        if (Graveyard.getConfig().fogSpawn(new ResourceLocation(Graveyard.MOD_ID, "graveyard_fog_particle"))) {
            // how much will spawn
            if (random.nextInt(Graveyard.getConfig().getParticle(new ResourceLocation(Graveyard.MOD_ID, "graveyard_fog_particle")).spawnChance) == 0) {
                world.addParticle(TGParticles.GRAVEYARD_FOG_PARTICLE, (double) pos.getX() + random.nextDouble(), (double) pos.getY() + random.nextDouble(), (double) pos.getZ() + random.nextDouble(), 0.0D, 0.0D, 0.0D);
            }

            if (random.nextInt(250) == 0) {
                world.addParticle(TGParticles.GRAVEYARD_HAND_PARTICLE, (double)pos.getX() + random.nextDouble(), (double)pos.getY() + 1.2D, (double)pos.getZ() + random.nextDouble(), 0.0D, 0.0D, 0.0D);
            }
        }
    }

}
