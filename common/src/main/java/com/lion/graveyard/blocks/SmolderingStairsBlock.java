package com.lion.graveyard.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class SmolderingStairsBlock extends StairsBlock {

    public SmolderingStairsBlock(BlockState baseState, Settings settings) {
        super(baseState, settings);
        setDefaultState(getDefaultState().with(SmolderingWood.SMOLDERING, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(SmolderingWood.SMOLDERING);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ActionResult result = SmolderingWood.use(state, world, pos, player, hand);
        return result == ActionResult.PASS ? super.onUse(state, world, pos, player, hand, hit) : result;
    }

    @Override
    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        return super.tryFillWithFluid(world, pos, state.with(SmolderingWood.SMOLDERING, false), fluidState);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        SmolderingWood.displayTick(state, world, pos, random);
    }
}
