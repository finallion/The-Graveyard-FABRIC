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

    /// Takes the other half with it. Called from `getStateForNeighborUpdate`, so breaking either half
    /// removes both without the block having to react to its own removal.
    public static BlockState onNeighborUpdate(BlockState state, Direction direction, BlockState neighbor, WorldAccess world, BlockPos pos) {
        DoubleBlockHalf half = state.get(HALF);
        boolean towardsOtherHalf = direction.getAxis() == Direction.Axis.Y
                && (half == DoubleBlockHalf.LOWER) == (direction == Direction.UP);

        if (towardsOtherHalf && (!neighbor.isOf(state.getBlock()) || neighbor.get(HALF) == half)) {
            return Blocks.AIR.getDefaultState();
        }

        return state;
    }

    /// Without this a creative player breaking the upper half gets a free copy from the lower one.
    public static void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (world.isClient || !player.isCreative() || state.get(HALF) != DoubleBlockHalf.UPPER) {
            return;
        }

        BlockPos below = pos.down();
        BlockState belowState = world.getBlockState(below);
        if (belowState.isOf(state.getBlock()) && belowState.get(HALF) == DoubleBlockHalf.LOWER) {
            world.setBlockState(below, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL | Block.SKIP_DROPS);
            world.syncWorldEvent(player, WorldEvents.BLOCK_BROKEN, below, Block.getRawIdFromState(belowState));
        }
    }

    public static boolean isLower(BlockState state) {
        return state.get(HALF) == DoubleBlockHalf.LOWER;
    }
}
