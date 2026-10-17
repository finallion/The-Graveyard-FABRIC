package com.lion.graveyard.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

/// Shared behaviour for decoration that stands two blocks tall.
///
/// Deliberately a helper and not a base class: the blocks that need it sit in unrelated hierarchies
/// already - a gravestone is a sign, an urn has an inventory - so they mix this in instead of
/// inheriting it. Each of them overrides the five hooks below with a one-line delegation.
///
/// The pattern follows the vanilla door and tall flower: the lower half is the real block, the upper
/// half is a marker that breaks with it and drops nothing on its own.
public final class TallDeco {

    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

    private TallDeco() {
    }

    /// Returns null when there is no room for the upper half, which makes the placement fail rather
    /// than leave a half block behind.
    @Nullable
    public static BlockState placementState(BlockPlaceContext ctx, BlockState lowerHalf) {
        BlockPos pos = ctx.getClickedPos();

        if (pos.getY() >= ctx.getLevel().getMaxBuildHeight() - 1 || !ctx.getLevel().getBlockState(pos.above()).canBeReplaced(ctx)) {
            return null;
        }

        return lowerHalf.setValue(HALF, DoubleBlockHalf.LOWER);
    }

    /// The upper half is placed by hand after the lower one, the way vanilla does it.
    public static void placeUpperHalf(Level world, BlockPos pos, BlockState state) {
        boolean waterlogged = world.getFluidState(pos.above()).getType() == Fluids.WATER;
        world.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER)
                .setValue(BlockStateProperties.WATERLOGGED, waterlogged), Block.UPDATE_ALL);
    }

    /// The lower half needs solid ground, the upper half needs its lower half.
    public static boolean canPlaceAt(BlockState state, LevelReader world, BlockPos pos) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockState below = world.getBlockState(pos.below());
            return below.is(state.getBlock()) && below.getValue(HALF) == DoubleBlockHalf.LOWER;
        }

        return world.getBlockState(pos.below()).isSolid();
    }

    /// Hanging decoration is anchored the other way round: the half that is clicked is the upper one,
    /// because that is the half touching the ceiling.
    @Nullable
    public static BlockState hangingPlacementState(BlockPlaceContext ctx, BlockState upperHalf) {
        BlockPos pos = ctx.getClickedPos();

        if (pos.getY() <= ctx.getLevel().getMinBuildHeight() || !ctx.getLevel().getBlockState(pos.below()).canBeReplaced(ctx)) {
            return null;
        }

        return upperHalf.setValue(HALF, DoubleBlockHalf.UPPER);
    }

    public static void placeLowerHalf(Level world, BlockPos pos, BlockState state) {
        boolean waterlogged = world.getFluidState(pos.below()).getType() == Fluids.WATER;
        world.setBlock(pos.below(), state.setValue(HALF, DoubleBlockHalf.LOWER)
                .setValue(BlockStateProperties.WATERLOGGED, waterlogged), Block.UPDATE_ALL);
    }

    /// The upper half needs a ceiling to hang from, the lower half needs its upper half.
    public static boolean canHangAt(BlockState state, LevelReader world, BlockPos pos) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            BlockState above = world.getBlockState(pos.above());
            return above.is(state.getBlock()) && above.getValue(HALF) == DoubleBlockHalf.UPPER;
        }

        BlockPos ceiling = pos.above();
        return world.getBlockState(ceiling).isFaceSturdy(world, ceiling, Direction.DOWN);
    }

    /// Takes the other half with it. Called from `getStateForNeighborUpdate`, so breaking either half
    /// removes both without the block having to react to its own removal.
    public static BlockState onNeighborUpdate(BlockState state, Direction direction, BlockState neighbor, LevelAccessor world, BlockPos pos) {
        DoubleBlockHalf half = state.getValue(HALF);
        boolean towardsOtherHalf = direction.getAxis() == Direction.Axis.Y
                && (half == DoubleBlockHalf.LOWER) == (direction == Direction.UP);

        if (towardsOtherHalf && (!neighbor.is(state.getBlock()) || neighbor.getValue(HALF) == half)) {
            return state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED)
                    ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
        }

        return state;
    }

    public static void onBreak(Level world, BlockPos pos, BlockState state, Player player) {
        if (world.isClientSide || !player.isCreative() || state.getValue(HALF) != DoubleBlockHalf.UPPER) {
            return;
        }

        BlockPos otherPos = pos.below();
        BlockState other = world.getBlockState(otherPos);

        if (other.is(state.getBlock()) && other.getValue(HALF) == DoubleBlockHalf.LOWER) {
            world.setBlock(otherPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
            world.levelEvent(player, LevelEvent.PARTICLES_DESTROY_BLOCK, otherPos, Block.getId(other));
        }
    }

    public static boolean isLower(BlockState state) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER;
    }
}
