package com.lion.graveyard.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/// Shared behaviour for decoration that hangs on a wall, written as a helper for the same reason as
/// [TallDeco]: the epitaph is a gravestone and cannot inherit a second base class.
///
/// `FACING` points away from the wall, the way vanilla ladders and wall signs read it.
public final class WallDeco {

    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    private WallDeco() {
    }

    /// Null for floor and ceiling: without a wall there is nothing to hang from, and the placement
    /// should fail rather than drop the piece on the ground.
    @Nullable
    public static BlockState placementState(ItemPlacementContext ctx, BlockState base) {
        Direction side = ctx.getSide();
        if (side.getAxis().isVertical()) {
            return null;
        }

        BlockState state = base
                .with(FACING, side)
                .with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER);
        return state.canPlaceAt(ctx.getWorld(), ctx.getBlockPos()) ? state : null;
    }

    public static boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        Direction back = state.get(FACING).getOpposite();
        BlockPos support = pos.offset(back);
        return world.getBlockState(support).isSideSolidFullSquare(world, support, state.get(FACING));
    }

    /// Falls when its wall goes, instead of hanging in the air.
    public static BlockState onNeighborUpdate(BlockState state, Direction direction, WorldAccess world, BlockPos pos) {
        return direction == state.get(FACING).getOpposite() && !canPlaceAt(state, world, pos)
                ? Blocks.AIR.getDefaultState()
                : state;
    }

    /// The four rotations of one plate, described once by how far it reaches from the wall and how much
    /// of the block face it covers.
    public static Map<Direction, VoxelShape> shapes(double margin, double bottom, double top, double depth) {
        double far = 16.0D - margin;
        return Map.of(
                Direction.NORTH, Block.createCuboidShape(margin, bottom, 16.0D - depth, far, top, 16.0D),
                Direction.SOUTH, Block.createCuboidShape(margin, bottom, 0.0D, far, top, depth),
                Direction.WEST, Block.createCuboidShape(16.0D - depth, bottom, margin, 16.0D, top, far),
                Direction.EAST, Block.createCuboidShape(0.0D, bottom, margin, depth, top, far));
    }
}
