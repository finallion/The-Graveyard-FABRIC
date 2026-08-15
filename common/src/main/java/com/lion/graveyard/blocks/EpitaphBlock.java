package com.lion.graveyard.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/// An inscription plate that hangs on a wall. Mechanically a gravestone - same block entity, same
/// text, same material derivation - only mounted instead of standing, which is the one thing the
/// mod's decoration had no example of.
public class EpitaphBlock extends CarvedGravestoneBlock {

    private static final Map<Direction, VoxelShape> SHAPES = Map.of(
            Direction.NORTH, Block.createCuboidShape(2.0D, 2.0D, 14.0D, 14.0D, 14.0D, 16.0D),
            Direction.SOUTH, Block.createCuboidShape(2.0D, 2.0D, 0.0D, 14.0D, 14.0D, 2.0D),
            Direction.WEST, Block.createCuboidShape(14.0D, 2.0D, 2.0D, 16.0D, 14.0D, 14.0D),
            Direction.EAST, Block.createCuboidShape(0.0D, 2.0D, 2.0D, 2.0D, 14.0D, 14.0D));

    public EpitaphBlock(Identifier texture) {
        super(texture);
    }

    @Override
    public boolean drawsFromItemModel() {
        return false;
    }

    @Override
    public double textHeight() {
        return 0.5D;
    }

    /// Just clear of the plate, which is two pixels deep.
    @Override
    public double textDepth() {
        return 0.16D;
    }

    /// Faces away from the wall it hangs on, so the placement side decides the direction.
    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction side = ctx.getSide();
        if (side.getAxis().isVertical()) {
            return null;
        }

        BlockState state = this.getDefaultState()
                .with(FACING, side)
                .with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER);
        return state.canPlaceAt(ctx.getWorld(), ctx.getBlockPos()) ? state : null;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        Direction back = state.get(FACING).getOpposite();
        return world.getBlockState(pos.offset(back)).isSideSolidFullSquare(world, pos.offset(back), state.get(FACING));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES.getOrDefault(state.get(FACING), SHAPES.get(Direction.NORTH));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }
}
