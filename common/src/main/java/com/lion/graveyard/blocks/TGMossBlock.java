package com.lion.graveyard.blocks;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.init.TGParticles;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MossBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class TGMossBlock extends MossBlock {
    private static final int FOG_SHEET_RARITY = 2;


    public TGMossBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(Blocks.MOSS_BLOCK);
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);

        if (Graveyard.getConfig().fogSpawn(Graveyard.id("graveyard_fog_particle"))) {
            if (random.nextInt(Graveyard.getConfig().getParticle(Graveyard.id("graveyard_fog_particle")).spawnChance * FOG_SHEET_RARITY) == 0) {
                if (!world.getBlockState(pos.up()).isSolidBlock(world, pos.up())) {
                    world.addParticle(TGParticles.GRAVEYARD_FOG_PARTICLE, pos.getX() + 0.5D + (random.nextDouble() * 2.0D - 1.0D) * 1.5D, pos.getY() + 1.0D + 0.05D + random.nextDouble() * 0.3D, pos.getZ() + 0.5D + (random.nextDouble() * 2.0D - 1.0D) * 1.5D, 0.0D, 0.0D, 0.0D);
                }
            }

            if (random.nextInt(250) == 0) {
                world.addParticle(TGParticles.GRAVEYARD_HAND_PARTICLE, (double)pos.getX() + random.nextDouble(), (double)pos.getY() + 1.2D, (double)pos.getZ() + random.nextDouble(), 0.0D, 0.0D, 0.0D);
            }
        }
    }

}
