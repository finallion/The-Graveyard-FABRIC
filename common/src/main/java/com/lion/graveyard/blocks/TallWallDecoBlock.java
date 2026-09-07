package com.lion.graveyard.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.state.StateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/// Decoration that hangs on a wall and is two blocks tall - [WallDecoBlock] and [TallDecoBlock] mixed,
/// because a piece the size of a whole skeleton does not fit in one block.
///
/// Only the lower half needs a wall behind it. Requiring one for both would refuse the piece on a wall
/// exactly two blocks high, which is the wall it is meant for.
public class TallWallDecoBlock extends Block implements Waterloggable {

    private final Map<Direction, VoxelShape> lowerShapes;
    private final Map<Direction, VoxelShape> upperShapes;

    public TallWallDecoBlock(AbstractBlock.Settings settings, Map<Direction, VoxelShape> lowerShapes, Map<Direction, VoxelShape> upperShapes) {
        super(settings);
        this.lowerShapes = lowerShapes;
        this.upperShapes = upperShapes;
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(WallDeco.FACING, Direction.NORTH)
                .with(WallDeco.WATERLOGGED, false)
                .with(TallDeco.HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WallDeco.FACING, WallDeco.WATERLOGGED, TallDeco.HALF);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos pos = ctx.getBlockPos();
        if (pos.getY() >= ctx.getWorld().getTopY() - 1 || !ctx.getWorld().getBlockState(pos.up()).canReplace(ctx)) {
            return null;
        }

        return WallDeco.placementState(ctx, this.getDefaultState().with(TallDeco.HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        TallDeco.placeUpperHalf(world, pos, state);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return state.get(TallDeco.HALF) == DoubleBlockHalf.UPPER
                ? isLowerHalfAt(state, world, pos.down())
                : WallDeco.canPlaceAt(state, world, pos);
    }

    private boolean isLowerHalfAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState below = world.getBlockState(pos);
        return below.isOf(state.getBlock()) && below.get(TallDeco.HALF) == DoubleBlockHalf.LOWER;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighbor, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WallDeco.WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        BlockState updated = TallDeco.onNeighborUpdate(state, direction, neighbor, world, pos);
        if (!updated.isAir() && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }

        return updated;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        TallDeco.onBreak(world, pos, state, player);
        super.onBreak(world, pos, state, player);
    }

    /// Only one half may drop, or a broken piece would hand out two.
    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        return TallDeco.isLower(state) ? super.getDroppedStacks(state, builder) : Collections.emptyList();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Map<Direction, VoxelShape> shapes = TallDeco.isLower(state) ? this.lowerShapes : this.upperShapes;
        return shapes.getOrDefault(state.get(WallDeco.FACING), shapes.get(Direction.NORTH));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WallDeco.WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(WallDeco.FACING, rotation.rotate(state.get(WallDeco.FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(WallDeco.FACING)));
    }
}
