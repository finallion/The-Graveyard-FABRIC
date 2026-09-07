package com.lion.graveyard.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
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
        // Centred on the plate and just clear of it, which is two pixels deep.
        super(texture, false, 0.5D, 0.16D, 0.0F);
    }

    /// Faces away from the wall it hangs on, so the placement side decides the direction.
    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return WallDeco.placementState(ctx, this.getDefaultState());
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return WallDeco.canPlaceAt(state, world, pos);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighbor, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        BlockState updated = WallDeco.onNeighborUpdate(state, direction, world, pos);
        return updated.isAir() ? updated : super.getStateForNeighborUpdate(state, direction, neighbor, world, pos, neighborPos);
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
