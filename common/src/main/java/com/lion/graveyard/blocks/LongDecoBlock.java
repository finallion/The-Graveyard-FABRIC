package com.lion.graveyard.blocks;

import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.LevelReader;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/// FACING points from the foot half to the head half.
public class LongDecoBlock extends Block implements SimpleWaterloggedBlock {

    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private final VoxelShape shape;

    public LongDecoBlock(BlockBehaviour.Properties settings, VoxelShape shape) {
        super(settings);
        this.shape = shape;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(PART, BedPart.FOOT)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART, WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockPos headPos = ctx.getClickedPos().relative(ctx.getHorizontalDirection());

        if (!ctx.getLevel().getBlockState(headPos).canBeReplaced(ctx)) {
            return null;
        }

        boolean waterlogged = ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER;
        return this.defaultBlockState()
                .setValue(FACING, ctx.getHorizontalDirection())
                .setValue(PART, BedPart.FOOT)
                .setValue(WATERLOGGED, waterlogged);
    }

    @Override
    public void setPlacedBy(
            Level world,
            BlockPos pos,
            BlockState state,
            @Nullable LivingEntity placer,
            ItemStack itemStack
    ) {
        BlockPos headPos = pos.relative(state.getValue(FACING));
        boolean waterlogged = world.getFluidState(headPos).getType() == Fluids.WATER;
        world.setBlock(headPos, state.setValue(PART, BedPart.HEAD).setValue(WATERLOGGED, waterlogged), Block.UPDATE_ALL);
    }

    private static BlockPos otherPartPos(BlockPos pos, BlockState state) {
        Direction towardHead = state.getValue(PART) == BedPart.FOOT ? state.getValue(FACING) : state.getValue(FACING).getOpposite();
        return pos.relative(towardHead);
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

        Direction towardOther = state.getValue(PART) == BedPart.FOOT ? state.getValue(FACING) : state.getValue(FACING).getOpposite();

        if (direction == towardOther && (!neighbor.is(this) || neighbor.getValue(PART) == state.getValue(PART))) {
            return state.getValue(WATERLOGGED) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
        }

        return state;
    }

    /// Only the foot may drop, or a broken piece would hand out two.
    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return state.getValue(PART) == BedPart.FOOT ? super.getDrops(state, builder) : Collections.emptyList();
    }

    @Override
    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        // Removing the foot here drops its loot through the neighbor update.
        if (!world.isClientSide() && player.isCreative() && state.getValue(PART) == BedPart.HEAD) {
            BlockPos otherPos = otherPartPos(pos, state);
            BlockState other = world.getBlockState(otherPos);

            if (other.is(this) && other.getValue(PART) != state.getValue(PART)) {
                world.setBlock(otherPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
                world.levelEvent(player, LevelEvent.PARTICLES_DESTROY_BLOCK, otherPos, Block.getId(other));
            }
        }

        return super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return this.shape;
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
