package com.lion.graveyard.blocks;

import com.mojang.serialization.MapCodec;
import com.lion.graveyard.blockentities.OssuaryBlockEntity;
import com.lion.graveyard.gui.OssuaryScreenHandler;
import com.lion.graveyard.init.TGBlockEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class OssuaryBlock extends BaseEntityBlock implements EntityBlock, BlockItemTooltip {

    @Override
    public void appendItemTooltip(Consumer<Component> tooltip) {
        tooltip.accept(Component.translatable("block.graveyard.ossuary.tooltip").withStyle(ChatFormatting.GRAY));
    }
    private static final Component TITLE = Component.translatable("container.ossuary");
    public static final EnumProperty<Direction> FACING;
    public static final BooleanProperty OPEN;

    public static final MapCodec<OssuaryBlock> CODEC = simpleCodec(OssuaryBlock::new);

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    public OssuaryBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(OPEN, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level world,
            BlockState state,
            BlockEntityType<T> type
    ) {
        return BaseEntityBlock.createTickerHelper(type, TGBlockEntities.OSSUARY_BLOCK_ENTITY.get(), OssuaryBlockEntity::tick);
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
            player.openMenu(state.getMenuProvider(world, pos));
            return InteractionResult.CONSUME;
        }
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        for (int i = 0; i < 10; i++) {
            world.addParticle(ParticleTypes.ASH, pos.getX() + random.nextIntBetweenInclusive(-1, 1) + 0.5D, pos.getY() + 1.0D, pos.getZ() + random.nextIntBetweenInclusive(-1, 1) + 0.5D, 0, 0, 0);

        }

    }

    @Nullable
    @Override
    public MenuProvider getMenuProvider(BlockState state, Level world, BlockPos pos) {
        return new SimpleMenuProvider((syncId, playerInventory, player) -> {
            return new OssuaryScreenHandler(syncId, playerInventory, ContainerLevelAccess.create(world, pos));
        }, TITLE);
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN);
    }

    @Override
    public boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new OssuaryBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    static {
        FACING = HorizontalDirectionalBlock.FACING;
        OPEN = BooleanProperty.create("open");
    }

}

