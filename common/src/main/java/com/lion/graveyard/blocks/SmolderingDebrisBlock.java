package com.lion.graveyard.blocks;

import com.mojang.serialization.MapCodec;
import com.lion.graveyard.blockentities.SmolderingDebrisBlockEntity;
import com.lion.graveyard.init.TGOverrunVillage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SmolderingDebrisBlock extends BaseEntityBlock {

    public static final MapCodec<SmolderingDebrisBlock> CODEC = simpleCodec(SmolderingDebrisBlock::new);

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    public SmolderingDebrisBlock(BlockBehaviour.Properties settings) {
        super(settings);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SmolderingDebrisBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level world,
            BlockState state,
            BlockEntityType<T> type
    ) {
        return world.isClientSide() ? createTickerHelper(type, TGOverrunVillage.SMOLDERING_DEBRIS_BLOCK_ENTITY.get(), SmolderingDebrisBlockEntity::clientTick) : null;
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        if (random.nextInt(10) == 0) {
            world.playLocalSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.6F, false);
        }

        if (random.nextInt(5) == 0) {
            world.addParticle(ParticleTypes.LAVA, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, random.nextFloat() / 2.0F, 5.0E-5D, random.nextFloat() / 2.0F);
        }

        SmolderingWood.ash(world, pos, random);
    }
}
