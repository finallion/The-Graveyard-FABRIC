package com.lion.graveyard.blocks;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.init.TGParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.data.worldgen.features.CaveFeatures;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableFeaturePlacerBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class TGMossBlock extends BonemealableFeaturePlacerBlock {
    private static final int FOG_SHEET_RARITY = 2;


    public TGMossBlock(BlockBehaviour.Properties settings) {
        super(CaveFeatures.MOSS_PATCH_BONEMEAL, settings);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack(Blocks.MOSS_BLOCK);
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        super.animateTick(state, world, pos, random);

        if (Graveyard.getConfig().fogSpawn(Graveyard.id("graveyard_fog_particle"))) {
            if (random.nextInt(Graveyard.getConfig().getParticle(Graveyard.id("graveyard_fog_particle")).spawnChance * FOG_SHEET_RARITY) == 0) {
                if (!world.getBlockState(pos.above()).isRedstoneConductor(world, pos.above())) {
                    world.addParticle(TGParticles.GRAVEYARD_FOG_PARTICLE, pos.getX() + 0.5D + (random.nextDouble() * 2.0D - 1.0D) * 1.5D, pos.getY() + 1.0D + 0.05D + random.nextDouble() * 0.3D, pos.getZ() + 0.5D + (random.nextDouble() * 2.0D - 1.0D) * 1.5D, 0.0D, 0.0D, 0.0D);
                }
            }

            if (random.nextInt(250) == 0) {
                world.addParticle(TGParticles.GRAVEYARD_HAND_PARTICLE, (double)pos.getX() + random.nextDouble(), (double)pos.getY() + 1.2D, (double)pos.getZ() + random.nextDouble(), 0.0D, 0.0D, 0.0D);
            }
        }
    }

}
