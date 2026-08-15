package com.lion.graveyard.blocks;

import com.lion.graveyard.blockentities.GravestoneBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
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

/// A gravestone that stands two blocks tall. Everything about the material and the inscription comes
/// from [CarvedGravestoneBlock]; this only adds the second half, through [TallDeco].
///
/// The block entity lives in the lower half alone - it holds the text and the material, and it is
/// what draws the whole stone, upper half included.
public class TallCarvedGravestoneBlock extends CarvedGravestoneBlock {

    private static final VoxelShape SHAPE_FACING_EW = Block.createCuboidShape(3.0D, 0.0D, 0.0D, 13.0D, 16.0D, 16.0D);
    private static final VoxelShape SHAPE_FACING_NS = Block.createCuboidShape(0.0D, 0.0D, 3.0D, 16.0D, 16.0D, 13.0D);

    public TallCarvedGravestoneBlock(Identifier texture) {
        super(texture);
        this.setDefaultState(this.getDefaultState().with(TallDeco.HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(TallDeco.HALF);
    }

    @Override
    public boolean drawsFromItemModel() {
        return false;
    }

    /// Further up than on the small stone: the inscription belongs on the shaft, not the plinth.
    @Override
    public double textHeight() {
        return 0.9D;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return TallDeco.isLower(state) ? new GravestoneBlockEntity(pos, state) : null;
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = super.getPlacementState(ctx);
        return state == null ? null : TallDeco.placementState(ctx, state);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        TallDeco.placeUpperHalf(world, pos, state);
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return TallDeco.canPlaceAt(state, world, pos);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighbor, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        BlockState updated = TallDeco.onNeighborUpdate(state, direction, neighbor, world, pos);
        return updated.isAir() ? updated : super.getStateForNeighborUpdate(state, direction, neighbor, world, pos, neighborPos);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        TallDeco.onBreak(world, pos, state, player);
        super.onBreak(world, pos, state, player);
    }

    /// Only the lower half carries the block entity, so only it knows the material - and only it may
    /// drop, or a broken stone would hand out two.
    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        return TallDeco.isLower(state) ? super.getDroppedStacks(state, builder) : Collections.emptyList();
    }

    /// Clicking the upper half edits the same stone.
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (TallDeco.isLower(state)) {
            return super.onUse(state, world, pos, player, hand, hit);
        }

        BlockPos lower = pos.down();
        return super.onUse(world.getBlockState(lower), world, lower, player, hand, hit);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return state.get(FACING).getAxis() == Direction.Axis.Z ? SHAPE_FACING_NS : SHAPE_FACING_EW;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getOutlineShape(state, world, pos, context);
    }
}
