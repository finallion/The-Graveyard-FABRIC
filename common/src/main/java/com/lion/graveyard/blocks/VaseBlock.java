package com.lion.graveyard.blocks;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VaseBlock extends Block implements SimpleWaterloggedBlock {
    public static final IntegerProperty VASES;
    public static final BooleanProperty WATERLOGGED;
    private static final VoxelShape VASE_SHAPE_ONE;
    private static final VoxelShape VASE_SHAPE_TWO;
    private static final VoxelShape VASE_SHAPE_THREE;
    private static final VoxelShape VASE_SHAPE_FOUR;

    public VaseBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(VASES, 1)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, VASES);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState blockState = ctx.getLevel().getBlockState(ctx.getClickedPos());

        if (blockState.is(this)) {
            return blockState.cycle(VASES);
        } else {
            FluidState fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());
            boolean waterlogged = fluidState.getType() == Fluids.WATER;
            return super.getStateForPlacement(ctx).setValue(WATERLOGGED, waterlogged);
        }
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return !context.isSecondaryUseActive() && context.getItemInHand().getItem() == this.asItem() && (Integer)state.getValue(VASES) < 4 ? true : super.canBeReplaced(state, context);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        switch((Integer)state.getValue(VASES)) {
            case 1:
            default:
                return VASE_SHAPE_ONE;
            case 2:
                return VASE_SHAPE_TWO;
            case 3:
                return VASE_SHAPE_THREE;
            case 4:
                return VASE_SHAPE_FOUR;
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return Block.canSupportCenter(world, pos.below(), Direction.UP);
    }

    static {
        VASES = BlockStateProperties.CANDLES;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        VASE_SHAPE_ONE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 12.0D, 12.0D);
        VASE_SHAPE_TWO = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 15.0D, 13.0D);
        VASE_SHAPE_THREE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
        VASE_SHAPE_FOUR = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    }
}
