package com.lion.graveyard.blockentities;

import com.lion.graveyard.init.TGOverrunVillage;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/// Carries no data. A block entity ticks on the client at any distance, a plain block does not.
public class SmolderingDebrisBlockEntity extends BlockEntity {

    public SmolderingDebrisBlockEntity(BlockPos pos, BlockState state) {
        super(TGOverrunVillage.SMOLDERING_DEBRIS_BLOCK_ENTITY.get(), pos, state);
    }

    public static void clientTick(
            Level world,
            BlockPos pos,
            BlockState state,
            SmolderingDebrisBlockEntity blockEntity
    ) {
        RandomSource random = world.getRandom();

        if (random.nextFloat() < 0.11F && !world.getBlockState(pos.above()).isSolidRender()) {
            boolean tallColumn = Math.floorMod(pos.hashCode(), 3) == 0;
            for (int i = 0; i < random.nextInt(2) + 2; i++) {
                CampfireBlock.makeParticles(world, pos, tallColumn, false);
            }
        }
    }
}
