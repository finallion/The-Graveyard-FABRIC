package com.lion.graveyard.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.enums.BedPart;
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
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/// FACING points from the foot half to the head half.
public class LongDecoBlock extends Block implements Waterloggable {

    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final EnumProperty<BedPart> PART = Properties.BED_PART;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    private final VoxelShape shape;

    public LongDecoBlock(AbstractBlock.Settings settings, VoxelShape shape) {
        super(settings);
        this.shape = shape;
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(PART, BedPart.FOOT)
                .with(WATERLOGGED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART, WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos headPos = ctx.getBlockPos().offset(ctx.getHorizontalPlayerFacing());

        if (!ctx.getWorld().getBlockState(headPos).canReplace(ctx)) {
            return null;
        }

        boolean waterlogged = ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER;
        return this.getDefaultState()
                .with(FACING, ctx.getHorizontalPlayerFacing())
                .with(PART, BedPart.FOOT)
                .with(WATERLOGGED, waterlogged);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        BlockPos headPos = pos.offset(state.get(FACING));
        boolean waterlogged = world.getFluidState(headPos).getFluid() == Fluids.WATER;
        world.setBlockState(headPos, state.with(PART, BedPart.HEAD).with(WATERLOGGED, waterlogged), Block.NOTIFY_ALL);
    }

    private static BlockPos otherPartPos(BlockPos pos, BlockState state) {
        Direction towardHead = state.get(PART) == BedPart.FOOT ? state.get(FACING) : state.get(FACING).getOpposite();
        return pos.offset(towardHead);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighbor, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        Direction towardOther = state.get(PART) == BedPart.FOOT ? state.get(FACING) : state.get(FACING).getOpposite();

        if (direction == towardOther && (!neighbor.isOf(this) || neighbor.get(PART) == state.get(PART))) {
            return state.get(WATERLOGGED) ? Blocks.WATER.getDefaultState() : Blocks.AIR.getDefaultState();
        }

        return state;
    }

    /// Only the foot may drop, or a broken piece would hand out two.
    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        return state.get(PART) == BedPart.FOOT ? super.getDroppedStacks(state, builder) : Collections.emptyList();
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        // Removing the foot here drops its loot through the neighbor update.
        if (!world.isClient && player.isCreative() && state.get(PART) == BedPart.HEAD) {
            BlockPos otherPos = otherPartPos(pos, state);
            BlockState other = world.getBlockState(otherPos);

            if (other.isOf(this) && other.get(PART) != state.get(PART)) {
                world.setBlockState(otherPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL | Block.SKIP_DROPS);
                world.syncWorldEvent(player, WorldEvents.BLOCK_BROKEN, otherPos, Block.getRawIdFromState(other));
            }
        }

        super.onBreak(world, pos, state, player);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.shape;
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
