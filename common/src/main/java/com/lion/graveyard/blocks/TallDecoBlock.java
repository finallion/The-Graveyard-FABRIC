package com.lion.graveyard.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
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
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/// Plain decoration that stands - or hangs - two blocks tall and faces a direction.
///
/// Everything specific to a piece is a constructor argument, so a new one costs a registration, a
/// model and a texture. Blocks that carry data of their own (a gravestone, an urn) cannot use this and
/// mix in [TallDeco] directly instead.
public class TallDecoBlock extends Block implements Waterloggable {

    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    private final VoxelShape lowerShape;
    private final VoxelShape upperShape;
    private final boolean hanging;

    public TallDecoBlock(AbstractBlock.Settings settings, VoxelShape shape) {
        this(settings, shape, shape, false);
    }

    public TallDecoBlock(AbstractBlock.Settings settings, VoxelShape lowerShape, VoxelShape upperShape) {
        this(settings, lowerShape, upperShape, false);
    }

    public TallDecoBlock(AbstractBlock.Settings settings, VoxelShape lowerShape, VoxelShape upperShape, boolean hanging) {
        super(settings);
        this.lowerShape = lowerShape;
        this.upperShape = upperShape;
        this.hanging = hanging;
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(WATERLOGGED, false)
                .with(TallDeco.HALF, hanging ? DoubleBlockHalf.UPPER : DoubleBlockHalf.LOWER));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED, TallDeco.HALF);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = this.getDefaultState()
                .with(FACING, ctx.getHorizontalPlayerFacing().getOpposite())
                .with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER);
        return this.hanging ? TallDeco.hangingPlacementState(ctx, state) : TallDeco.placementState(ctx, state);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (this.hanging) {
            TallDeco.placeLowerHalf(world, pos, state);
        } else {
            TallDeco.placeUpperHalf(world, pos, state);
        }
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return this.hanging ? TallDeco.canHangAt(state, world, pos) : TallDeco.canPlaceAt(state, world, pos);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighbor, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        BlockState updated = TallDeco.onNeighborUpdate(state, direction, neighbor, world, pos);
        // A hanging piece also falls when its ceiling goes, which no half-to-half check would catch.
        if (this.hanging && !updated.isAir() && !state.canPlaceAt(world, pos)) {
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
        return TallDeco.isLower(state) ? this.lowerShape : this.upperShape;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }
}
