package com.lion.graveyard.blocks;

import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TallDecoBlock extends Block implements SimpleWaterloggedBlock {

    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private final VoxelShape lowerShape;
    private final VoxelShape upperShape;
    private final boolean hanging;

    public TallDecoBlock(BlockBehaviour.Properties settings, VoxelShape shape) {
        this(settings, shape, shape, false);
    }

    public TallDecoBlock(BlockBehaviour.Properties settings, VoxelShape lowerShape, VoxelShape upperShape) {
        this(settings, lowerShape, upperShape, false);
    }

    public TallDecoBlock(
            BlockBehaviour.Properties settings,
            VoxelShape lowerShape,
            VoxelShape upperShape,
            boolean hanging
    ) {
        super(settings);
        this.lowerShape = lowerShape;
        this.upperShape = upperShape;
        this.hanging = hanging;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false)
                .setValue(TallDeco.HALF, hanging ? DoubleBlockHalf.UPPER : DoubleBlockHalf.LOWER));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED, TallDeco.HALF);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState state = this.defaultBlockState()
                .setValue(FACING, ctx.getHorizontalDirection().getOpposite())
                .setValue(WATERLOGGED, ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER);
        return this.hanging ? TallDeco.hangingPlacementState(ctx, state) : TallDeco.placementState(ctx, state);
    }

    @Override
    public void setPlacedBy(
            Level world,
            BlockPos pos,
            BlockState state,
            @Nullable LivingEntity placer,
            ItemStack itemStack
    ) {
        if (this.hanging) {
            TallDeco.placeLowerHalf(world, pos, state);
        } else {
            TallDeco.placeUpperHalf(world, pos, state);
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return this.hanging ? TallDeco.canHangAt(state, world, pos) : TallDeco.canPlaceAt(state, world, pos);
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            LevelReader world,
            ScheduledTickAccess tickAccess,
            BlockPos pos,
            Direction direction,
            BlockPos neighborPos,
            BlockState neighbor,
            RandomSource random
    ) {
        if (state.getValue(WATERLOGGED)) {
            tickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }

        BlockState updated = TallDeco.onNeighborUpdate(state, direction, neighbor, world, pos);
        // A hanging piece also falls when its ceiling goes, which no half-to-half check would catch.
        if (this.hanging && !updated.isAir() && !state.canSurvive(world, pos)) {
            return Blocks.AIR.defaultBlockState();
        }

        return updated;
    }

    @Override
    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        TallDeco.onBreak(world, pos, state, player);
        return super.playerWillDestroy(world, pos, state, player);
    }

    /// Only one half may drop, or a broken piece would hand out two.
    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return TallDeco.isLower(state) ? super.getDrops(state, builder) : Collections.emptyList();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return TallDeco.isLower(state) ? this.lowerShape : this.upperShape;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
}
