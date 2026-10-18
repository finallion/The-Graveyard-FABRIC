package com.lion.graveyard.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;

public class SmolderingSlabBlock extends SlabBlock {

    public SmolderingSlabBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(SmolderingWood.SMOLDERING, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SmolderingWood.SMOLDERING);
    }

    @Override
    protected InteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level world,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        InteractionResult result = SmolderingWood.use(stack, state, world, pos, player, hand);
        return result == InteractionResult.TRY_WITH_EMPTY_HAND ? super.useItemOn(stack, state, world, pos, player, hand, hit) : result;
    }

    @Override
    public boolean placeLiquid(LevelAccessor world, BlockPos pos, BlockState state, FluidState fluidState) {
        return super.placeLiquid(world, pos, state.setValue(SmolderingWood.SMOLDERING, false), fluidState);
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        SmolderingWood.displayTick(state, world, pos, random);
    }
}
