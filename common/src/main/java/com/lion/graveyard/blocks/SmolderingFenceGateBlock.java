package com.lion.graveyard.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.BlockHitResult;

public class SmolderingFenceGateBlock extends FenceGateBlock {

    public SmolderingFenceGateBlock(Properties settings, WoodType woodType) {
        super(settings, woodType);
        registerDefaultState(defaultBlockState().setValue(SmolderingWood.SMOLDERING, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SmolderingWood.SMOLDERING);
    }

    @Override
    public InteractionResult use(
            BlockState state,
            Level world,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        InteractionResult result = SmolderingWood.use(state, world, pos, player, hand);
        return result == InteractionResult.PASS ? super.use(state, world, pos, player, hand, hit) : result;
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        SmolderingWood.displayTick(state, world, pos, random);
    }
}
