package com.lion.graveyard.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.WorldView;
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

    public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;

    private TallDeco() {
    }

    /// Returns null when there is no room for the upper half, which makes the placement fail rather
    /// than leave a half block behind.
    @Nullable
    public static BlockState placementState(ItemPlacementContext ctx, BlockState lowerHalf) {
        BlockPos pos = ctx.getBlockPos();

        if (pos.getY() >= ctx.getWorld().getTopY() - 1 || !ctx.getWorld().getBlockState(pos.up()).canReplace(ctx)) {
            return null;
        }

        return lowerHalf.with(HALF, DoubleBlockHalf.LOWER);
    }

    /// The upper half is placed by hand after the lower one, the way vanilla does it.
    public static void placeUpperHalf(World world, BlockPos pos, BlockState state) {
        boolean waterlogged = world.getFluidState(pos.up()).getFluid() == Fluids.WATER;
        world.setBlockState(pos.up(), state.with(HALF, DoubleBlockHalf.UPPER)
                .with(Properties.WATERLOGGED, waterlogged), Block.NOTIFY_ALL);
    }

    /// The lower half needs solid ground, the upper half needs its lower half.
    public static boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        if (state.get(HALF) == DoubleBlockHalf.UPPER) {
            BlockState below = world.getBlockState(pos.down());
            return below.isOf(state.getBlock()) && below.get(HALF) == DoubleBlockHalf.LOWER;
        }

        return world.getBlockState(pos.down()).isSolid();
    }

    /// Hanging decoration is anchored the other way round: the half that is clicked is the upper one,
    /// because that is the half touching the ceiling.
    @Nullable
    public static BlockState hangingPlacementState(ItemPlacementContext ctx, BlockState upperHalf) {
        BlockPos pos = ctx.getBlockPos();

        if (pos.getY() <= ctx.getWorld().getBottomY() || !ctx.getWorld().getBlockState(pos.down()).canReplace(ctx)) {
            return null;
        }

        return upperHalf.with(HALF, DoubleBlockHalf.UPPER);
    }

    public static void placeLowerHalf(World world, BlockPos pos, BlockState state) {
        boolean waterlogged = world.getFluidState(pos.down()).getFluid() == Fluids.WATER;
        world.setBlockState(pos.down(), state.with(HALF, DoubleBlockHalf.LOWER)
                .with(Properties.WATERLOGGED, waterlogged), Block.NOTIFY_ALL);
    }

    /// The upper half needs a ceiling to hang from, the lower half needs its upper half.
    public static boolean canHangAt(BlockState state, WorldView world, BlockPos pos) {
        if (state.get(HALF) == DoubleBlockHalf.LOWER) {
            BlockState above = world.getBlockState(pos.up());
            return above.isOf(state.getBlock()) && above.get(HALF) == DoubleBlockHalf.UPPER;
        }

        BlockPos ceiling = pos.up();
        return world.getBlockState(ceiling).isSideSolidFullSquare(world, ceiling, Direction.DOWN);
    }

    /// Takes the other half with it. Called from `getStateForNeighborUpdate`, so breaking either half
    /// removes both without the block having to react to its own removal.
    public static BlockState onNeighborUpdate(BlockState state, Direction direction, BlockState neighbor, WorldAccess world, BlockPos pos) {
        DoubleBlockHalf half = state.get(HALF);
        boolean towardsOtherHalf = direction.getAxis() == Direction.Axis.Y
                && (half == DoubleBlockHalf.LOWER) == (direction == Direction.UP);

        if (towardsOtherHalf && (!neighbor.isOf(state.getBlock()) || neighbor.get(HALF) == half)) {
            return state.contains(Properties.WATERLOGGED) && state.get(Properties.WATERLOGGED)
                    ? Blocks.WATER.getDefaultState() : Blocks.AIR.getDefaultState();
        }

        return state;
    }

    public static void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (world.isClient || !player.isCreative() || state.get(HALF) != DoubleBlockHalf.UPPER) {
            return;
        }

        BlockPos otherPos = pos.down();
        BlockState other = world.getBlockState(otherPos);

        if (other.isOf(state.getBlock()) && other.get(HALF) == DoubleBlockHalf.LOWER) {
            world.setBlockState(otherPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL | Block.SKIP_DROPS);
            world.syncWorldEvent(player, WorldEvents.BLOCK_BROKEN, otherPos, Block.getRawIdFromState(other));
        }
    }

    public static boolean isLower(BlockState state) {
        return state.get(HALF) == DoubleBlockHalf.LOWER;
    }
}
