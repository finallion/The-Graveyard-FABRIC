package com.lion.graveyard.blocks;

import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CandleHolderBlock extends Block implements SimpleWaterloggedBlock {
    protected static final VoxelShape SHAPE_NORTH = Block.box(4.0D, 2.0D, 0.0D, 12.0D, 16.0D, 11.0D);
    protected static final VoxelShape SHAPE_SOUTH = Block.box(4.0D, 2.0D, 5.0D, 12.0D, 16.0D, 16.0D);
    protected static final VoxelShape SHAPE_EAST = Block.box(5.0D, 2.0D, 4.0D, 16.0D, 16.0D, 12.0D);
    protected static final VoxelShape SHAPE_WEST = Block.box(0.0D, 2.0D, 4.0D, 11.0D, 16.0D, 12.0D);
    public static final DirectionProperty FACING;
    public static final BooleanProperty WATERLOGGED;

    public CandleHolderBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false));
    }

    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        switch (state.getValue(FACING)) {
            case SOUTH -> {
                return SHAPE_SOUTH;
            }
            case EAST -> {
                return SHAPE_EAST;
            }
            case WEST -> {
                return SHAPE_WEST;
            }
            default -> {
                return SHAPE_NORTH;
            }
        }
    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        FluidState fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());
        return this.defaultBlockState()
                .setValue(FACING, ctx.getHorizontalDirection())
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }


    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if ((Boolean)state.getValue(WATERLOGGED)) {
            world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }

        return direction == Direction.DOWN ? (BlockState)state: super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    public FluidState getFluidState(BlockState state) {
        return (Boolean)state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }


    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    public BlockState rotate(BlockState state, Rotation rotation) {
        return (BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
    }


    static {
        FACING = HorizontalDirectionalBlock.FACING;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
    }
}
