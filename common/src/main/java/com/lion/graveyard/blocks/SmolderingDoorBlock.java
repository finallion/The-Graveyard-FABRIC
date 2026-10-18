package com.lion.graveyard.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.phys.BlockHitResult;

public class SmolderingDoorBlock extends DoorBlock {

    public SmolderingDoorBlock(Properties settings, BlockSetType setType) {
        super(setType, settings);
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
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        SmolderingWood.displayTick(state, world, pos, random);
    }
}
