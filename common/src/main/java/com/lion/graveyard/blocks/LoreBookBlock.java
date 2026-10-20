package com.lion.graveyard.blocks;

import com.mojang.serialization.MapCodec;
import com.lion.graveyard.blockentities.LoreBookBlockEntity;
import com.lion.graveyard.chronicle.Chronicle;
import com.lion.graveyard.init.TGItems;
import com.lion.graveyard.item.DiaryPageItem;
import com.lion.graveyard.init.TGAdvancements;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
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
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LoreBookBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {

    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<Variant> VARIANT = EnumProperty.create("variant", Variant.class);

    public enum Variant implements StringRepresentable {
        CLOSED("closed"), OPEN("open"), LEAF("leaf");

        private final String name;

        Variant(String name) { this.name = name; }

        @Override public String getSerializedName() { return this.name; }

        public Variant next() { return values()[(this.ordinal() + 1) % values().length]; }
    }

    private static final VoxelShape SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 3.0D, 13.0D);

    public static final MapCodec<LoreBookBlock> CODEC = simpleCodec(LoreBookBlock::new);

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    public LoreBookBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false)
                .setValue(VARIANT, Variant.OPEN));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED, VARIANT);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean water = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(WATERLOGGED, water);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LoreBookBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(
            Level world,
            BlockPos pos,
            BlockState state,
            @Nullable net.minecraft.world.entity.LivingEntity placer,
            ItemStack stack
    ) {
        String page = DiaryPageItem.pageOf(stack);

        if (page != null && world.getBlockEntity(pos) instanceof LoreBookBlockEntity book) {
            book.setPage(page);

            if (placer instanceof ServerPlayer serverPlayer) {
                TGAdvancements.PLACE_LORE_BOOK.trigger(serverPlayer);
            }
        }
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level world,
            BlockPos pos,
            Player player,
            BlockHitResult hit
    ) {
        if (player.isShiftKeyDown()) {
            if (!player.mayBuild()) {
                return InteractionResult.PASS;
            }

            if (!world.isClientSide()) {
                world.setBlock(pos, state.setValue(VARIANT, state.getValue(VARIANT).next()), Block.UPDATE_ALL);
            }

            return InteractionResult.SUCCESS;
        }

        if (!(world.getBlockEntity(pos) instanceof LoreBookBlockEntity book) || book.getPage() == null) {
            return InteractionResult.PASS;
        }

        if (player instanceof ServerPlayer serverPlayer && !Chronicle.find(serverPlayer, book.getPage())) {
            serverPlayer.sendOverlayMessage(Component.translatable("chronicle.graveyard.already_found").withStyle(ChatFormatting.GRAY));
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        BlockEntity entity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);

        if (entity instanceof LoreBookBlockEntity book && book.getPage() != null) {
            return List.of(DiaryPageItem.of(TGItems.DIARY_PAGE.get(), book.getPage()));
        }

        return List.of(new ItemStack(TGItems.DIARY_PAGE.get()));
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state, boolean includeData) {
        if (world.getBlockEntity(pos) instanceof LoreBookBlockEntity book && book.getPage() != null) {
            return DiaryPageItem.of(TGItems.DIARY_PAGE.get(), book.getPage());
        }

        return new ItemStack(TGItems.DIARY_PAGE.get());
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            LevelReader world,
            ScheduledTickAccess tickAccess,
            BlockPos pos,
            Direction direction,
            BlockPos neighborPos,
            BlockState neighborState,
            RandomSource random
    ) {
        if (state.getValue(WATERLOGGED)) {
            tickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }

        return super.updateShape(state, world, tickAccess, pos, direction, neighborPos, neighborState, random);
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
