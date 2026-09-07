package com.lion.graveyard.blocks;

import com.lion.graveyard.blockentities.LoreBookBlockEntity;
import com.lion.graveyard.chronicle.Chronicle;
import com.lion.graveyard.init.TGItems;
import com.lion.graveyard.item.DiaryPageItem;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/// A chronicle page lying in the world. Reading it records the page and leaves the block for the
/// next reader; breaking it hands over the loose leaf with the same page id.
public class LoreBookBlock extends BlockWithEntity implements Waterloggable {

    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final EnumProperty<Variant> VARIANT = EnumProperty.of("variant", Variant.class);

    /// The look is a state, not a page: any page can lie there as a closed book, an open one, a
    /// loose leaf or a scroll. Cycled with a sneak-click so builders can pick without a wand.
    public enum Variant implements StringIdentifiable {
        CLOSED("closed"), OPEN("open"), LEAF("leaf"), SCROLL("scroll");

        private final String name;

        Variant(String name) { this.name = name; }

        @Override public String asString() { return this.name; }

        public Variant next() { return values()[(this.ordinal() + 1) % values().length]; }
    }

    private static final VoxelShape SHAPE = Block.createCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 3.0D, 13.0D);

    public LoreBookBlock() {
        super(AbstractBlock.Settings.create().nonOpaque().noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOL));
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH).with(WATERLOGGED, false).with(VARIANT, Variant.OPEN));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED, VARIANT);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        boolean water = context.getWorld().getFluidState(context.getBlockPos()).getFluid() == Fluids.WATER;
        return this.getDefaultState().with(FACING, context.getHorizontalPlayerFacing().getOpposite()).with(WATERLOGGED, water);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LoreBookBlockEntity(pos, state);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable net.minecraft.entity.LivingEntity placer, ItemStack stack) {
        String page = DiaryPageItem.pageOf(stack);
        if (page != null && world.getBlockEntity(pos) instanceof LoreBookBlockEntity book) {
            book.setPage(page);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (player.isSneaking()) {
            if (!world.isClient()) world.setBlockState(pos, state.with(VARIANT, state.get(VARIANT).next()), Block.NOTIFY_ALL);
            return ActionResult.success(world.isClient());
        }
        if (!(world.getBlockEntity(pos) instanceof LoreBookBlockEntity book) || book.getPage() == null) {
            return ActionResult.PASS;
        }
        if (player instanceof ServerPlayerEntity serverPlayer && !Chronicle.find(serverPlayer, book.getPage())) {
            serverPlayer.sendMessage(Text.translatable("chronicle.graveyard.already_found").formatted(Formatting.GRAY), true);
        }
        return ActionResult.success(world.isClient());
    }

    /// The loose leaf keeps the page, so the block can be picked up and laid down elsewhere.
    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        BlockEntity entity = builder.getOptional(LootContextParameters.BLOCK_ENTITY);
        if (entity instanceof LoreBookBlockEntity book && book.getPage() != null) {
            return List.of(DiaryPageItem.of(TGItems.DIARY_PAGE.get(), book.getPage()));
        }
        return List.of(new ItemStack(TGItems.DIARY_PAGE.get()));
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        if (world.getBlockEntity(pos) instanceof LoreBookBlockEntity book && book.getPage() != null) {
            return DiaryPageItem.of(TGItems.DIARY_PAGE.get(), book.getPage());
        }
        return new ItemStack(TGItems.DIARY_PAGE.get());
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
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
