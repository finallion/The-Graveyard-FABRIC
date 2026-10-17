package com.lion.graveyard.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.WoodType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class SmolderingFenceGateBlock extends FenceGateBlock {

    public SmolderingFenceGateBlock(Settings settings, WoodType woodType) {
        super(settings, woodType);
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
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        SmolderingWood.displayTick(state, world, pos, random);
    }
}
