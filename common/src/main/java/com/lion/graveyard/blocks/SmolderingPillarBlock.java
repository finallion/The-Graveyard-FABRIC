package com.lion.graveyard.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.function.Supplier;

public class SmolderingPillarBlock extends PillarBlock {

    private final Supplier<Block> stripped;

    public SmolderingPillarBlock(Settings settings) {
        this(settings, null);
    }

    public SmolderingPillarBlock(Settings settings, Supplier<Block> stripped) {
        super(settings);
        this.stripped = stripped;
        setDefaultState(getDefaultState().with(SmolderingWood.SMOLDERING, false));
    }

    /// The axe result keeps the axis and the smoldering state. It is null when the block has no stripped form.
    public BlockState strippedState(BlockState state) {
        if (this.stripped == null) {
            return null;
        }

        return this.stripped.get().getStateWithProperties(state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(SmolderingWood.SMOLDERING);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return SmolderingWood.use(state, world, pos, player, hand);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        SmolderingWood.displayTick(state, world, pos, random);
    }
}
