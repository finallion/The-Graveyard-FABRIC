package com.lion.graveyard.blockentities;

import com.lion.graveyard.init.TGOverrunVillage;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

/// Carries no data. A block entity ticks on the client at any distance, a plain block does not.
public class SmolderingDebrisBlockEntity extends BlockEntity {

    public SmolderingDebrisBlockEntity(BlockPos pos, BlockState state) {
        super(TGOverrunVillage.SMOLDERING_DEBRIS_BLOCK_ENTITY.get(), pos, state);
    }

    public static void clientTick(World world, BlockPos pos, BlockState state, SmolderingDebrisBlockEntity blockEntity) {
        Random random = world.random;
        if (random.nextFloat() < 0.11F && !world.getBlockState(pos.up()).isOpaqueFullCube(world, pos.up())) {
            boolean tallColumn = Math.floorMod(pos.hashCode(), 3) == 0;
            for (int i = 0; i < random.nextInt(2) + 2; i++) {
                CampfireBlock.spawnSmokeParticle(world, pos, tallColumn, false);
            }
        }
    }
}
