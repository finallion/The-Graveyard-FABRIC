package com.lion.graveyard.blocks;

import com.mojang.serialization.MapCodec;
import com.lion.graveyard.blockentities.UrnBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class UrnBlock extends BaseEntityBlock implements SimpleWaterloggedBlock, EntityBlock {
    public static final BooleanProperty WATERLOGGED;
    public static final EnumProperty<Direction> FACING;
    public static final BooleanProperty OPEN;
    private static final VoxelShape LARGE_URN = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);
    private static final VoxelShape SMALL_URN = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 10.0D, 13.0D);

    public static final MapCodec<UrnBlock> CODEC = simpleCodec(UrnBlock::new);

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    public UrnBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false)
                .setValue(OPEN, false));
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state) {
        return true;
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
        return 1.0F;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, FACING, OPEN);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        FluidState fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());
        return this.defaultBlockState()
                .setValue(FACING, ctx.getHorizontalDirection().getOpposite())
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (state.getBlock().toString().contains("small")) {
            return SMALL_URN;
        } else {
            return LARGE_URN;
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (state.getBlock().toString().contains("small")) {
            return SMALL_URN;
        } else {
            return LARGE_URN;
        }
    }

    @Override
    public boolean isCollisionShapeFullBlock(BlockState state, BlockGetter world, BlockPos pos) {
        return true;
    }


    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level world,
            BlockPos pos,
            Player player,
            BlockHitResult hit
    ) {
        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof UrnBlockEntity) {
                player.openMenu((UrnBlockEntity)blockEntity);
            }

            return InteractionResult.CONSUME;
        }
    }


    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, net.minecraft.util.RandomSource random) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof UrnBlockEntity) {
            ((UrnBlockEntity)blockEntity).onScheduledTick();
        }

    }

    @Override
    public boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }



    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new UrnBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return false;
    }


    static {
        FACING = HorizontalDirectionalBlock.FACING;
        OPEN = BlockStateProperties.OPEN;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
    }
}
