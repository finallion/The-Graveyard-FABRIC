package com.lion.graveyard.blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
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

    public VaseBlock() {
        super(BlockBehaviour.Properties.of().instabreak().noCollission().noOcclusion().sound(SoundType.GLASS));
        this.registerDefaultState(this.getStateDefinition().any().setValue(VASES, 1).setValue(WATERLOGGED, false));
    }
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(VASES, WATERLOGGED);
    }

    public BlockState getStateForPlacement(BlockPlaceContext p_196258_1_) {
        BlockState blockstate = p_196258_1_.getLevel().getBlockState(p_196258_1_.getClickedPos());
        if (blockstate.is(this)) {
            return blockstate.setValue(VASES, Integer.valueOf(Math.min(4, blockstate.getValue(VASES) + 1)));
        } else {
            FluidState fluidstate = p_196258_1_.getLevel().getFluidState(p_196258_1_.getClickedPos());
            boolean flag = fluidstate.getType() == Fluids.WATER;
            return super.getStateForPlacement(p_196258_1_).setValue(WATERLOGGED, Boolean.valueOf(flag));
        }
    }

    public FluidState getFluidState(BlockState state) {
        return (Boolean)state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    public boolean canBeReplaced(BlockState p_152814_, BlockPlaceContext p_152815_) {
        return !p_152815_.isSecondaryUseActive() && p_152815_.getItemInHand().getItem() == this.asItem() && p_152814_.getValue(VASES) < 4 ? true : super.canBeReplaced(p_152814_, p_152815_);
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter p_220071_2_, BlockPos p_220071_3_, CollisionContext p_220071_4_) {
        return switch (state.getValue(VASES)) {
            default -> VASE_SHAPE_ONE;
            case 2 -> VASE_SHAPE_TWO;
            case 3 -> VASE_SHAPE_THREE;
            case 4 -> VASE_SHAPE_FOUR;
        };
    }

    public boolean canSurvive(BlockState p_152829_, LevelReader p_152830_, BlockPos p_152831_) {
        return Block.canSupportCenter(p_152830_, p_152831_.below(), Direction.UP);
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
