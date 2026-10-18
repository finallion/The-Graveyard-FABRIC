package com.lion.graveyard.blocks;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;

public class SmolderingPillarBlock extends RotatedPillarBlock {

    private final Supplier<Block> stripped;

    public SmolderingPillarBlock(Properties settings) {
        this(settings, null);
    }

    public SmolderingPillarBlock(Properties settings, Supplier<Block> stripped) {
        super(settings);
        this.stripped = stripped;
        registerDefaultState(defaultBlockState().setValue(SmolderingWood.SMOLDERING, false));
    }

    /// The axe result keeps the axis and the smoldering state. It is null when the block has no stripped form.
    public BlockState strippedState(BlockState state) {
        if (this.stripped == null) {
            return null;
        }

        return this.stripped.get().withPropertiesOf(state);
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
        return SmolderingWood.use(stack, state, world, pos, player, hand);
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        SmolderingWood.displayTick(state, world, pos, random);
    }
}
