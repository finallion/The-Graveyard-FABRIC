package com.lion.graveyard.blocks;

import com.mojang.serialization.MapCodec;
import com.lion.graveyard.blockentities.SarcophagusBlockEntity;
import com.lion.graveyard.blockentities.enums.SarcophagusPart;
import com.lion.graveyard.entities.MobSpawnGate;
import com.lion.graveyard.entities.WraithEntity;
import com.lion.graveyard.init.TGAdvancements;
import com.lion.graveyard.init.TGBlockEntities;
import com.lion.graveyard.init.TGEntities;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.LevelReader;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;

import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.LidBlockEntity;
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

import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;

public class SarcophagusBlock extends BaseEntityBlock implements SimpleWaterloggedBlock, EntityBlock {
    public static final BooleanProperty WATERLOGGED;
    public static final BooleanProperty OPEN;
    public static final BooleanProperty PLAYER_PLACED;
    public static final BooleanProperty IS_COFFIN = BooleanProperty.create("is_coffin");
    public static final EnumProperty<Direction> FACING;
    protected static final VoxelShape DOUBLE_NORTH_SHAPE;
    protected static final VoxelShape DOUBLE_SOUTH_SHAPE;
    protected static final VoxelShape DOUBLE_WEST_SHAPE;
    protected static final VoxelShape DOUBLE_EAST_SHAPE;
    public static final EnumProperty<SarcophagusPart> PART = EnumProperty.create("part", SarcophagusPart.class);
    private final Supplier<Item> lid;
    private final Supplier<Item> base;

    public static final MapCodec<SarcophagusBlock> CODEC = simpleCodec(properties -> new SarcophagusBlock(properties, false, () -> Items.AIR, () -> Items.AIR));

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    public SarcophagusBlock(Properties settings, boolean isCoffin, Supplier<Item> lid, Supplier<Item> base) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(WATERLOGGED, false)
                .setValue(OPEN, false)
                .setValue(PART, SarcophagusPart.FOOT)
                .setValue(PLAYER_PLACED, false)
                .setValue(IS_COFFIN, isCoffin));
        this.base = base;
        this.lid = lid;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, OPEN, FACING, PART, PLAYER_PLACED, IS_COFFIN);
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
        if (direction == getDirectionTowardsOtherPart(state.getValue(PART), state.getValue(FACING))) {
            return neighborState.is(this) && neighborState.getValue(PART) != state.getValue(PART) ? state : Blocks.AIR.defaultBlockState();
        } else {
            return super.updateShape(state, world, tickAccess, pos, direction, neighborPos, neighborState, random);
        }
    }

    private static Direction getDirectionTowardsOtherPart(SarcophagusPart part, Direction direction) {
        return part == SarcophagusPart.FOOT ? direction : direction.getOpposite();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        switch (state.getValue(FACING)) {
            case NORTH:
            default:
                return DOUBLE_NORTH_SHAPE;
            case SOUTH:
                return DOUBLE_SOUTH_SHAPE;
            case WEST:
                return DOUBLE_WEST_SHAPE;
            case EAST:
                return DOUBLE_EAST_SHAPE;
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
        Random random = new Random();

        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        } else {
            BlockPos original = pos;

            if (state.getValue(PART) == SarcophagusPart.HEAD) {
                pos = pos.relative(state.getValue(FACING).getOpposite());
            }

            BlockEntity blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof SarcophagusBlockEntity) {
                player.openMenu((SarcophagusBlockEntity) blockEntity);
            }

            spawnGhost(state, world, original, player, random);

            return InteractionResult.CONSUME;
        }
    }

    public static void spawnGhost(BlockState state, Level world, BlockPos pos, Player player, Random random) {
        if (!state.getValue(PLAYER_PLACED) && random.nextInt(4) == 0 && pos.getY() < 62) {
            boolean allowed = !MobSpawnGate.blocks(TGEntities.WRAITH.get(), EntitySpawnReason.STRUCTURE);
            WraithEntity ghost = null;

            if (allowed) {
                BlockPos entityPos = pos;
                for (int i = 0; i < 10; i++) {
                    entityPos = player.blockPosition().offset(-2 + random.nextInt(5), 1, -2 + random.nextInt(5));

                    if (world.getBlockState(entityPos).isAir() && world.getBlockState(entityPos.above()).isAir()) {
                        break;
                    }
                }
                ghost = TGEntities.WRAITH.get().create(world, EntitySpawnReason.TRIGGERED);

                if (ghost != null) {
                    ghost.snapTo(entityPos, 0.0F, 0.0F);
                    ghost.startSpawnAnimation();
                    world.addFreshEntity(ghost);
                }
            }

            world.setBlock(pos, state.setValue(PLAYER_PLACED, true), 3);
            BlockPos otherPartPos = pos.relative(getDirectionTowardsOtherPart(state.getValue(PART), state.getValue(FACING)));
            BlockState otherPart = world.getBlockState(otherPartPos);

            if (ghost != null && player instanceof ServerPlayer) {
                TGAdvancements.SPAWN_WRAITH.trigger((ServerPlayer) player);
            }

            world.setBlock(otherPartPos, otherPart.setValue(PLAYER_PLACED, true), 3);
        }
    }


    @Override
    public void setPlacedBy(
            Level world,
            BlockPos pos,
            BlockState state,
            @Nullable LivingEntity placer,
            ItemStack itemStack
    ) {
        super.setPlacedBy(world, pos, state, placer, itemStack);

        if (!world.isClientSide()) {
            BlockPos blockPos = pos.relative(state.getValue(FACING));
            world.setBlockAndUpdate(pos, state.setValue(PLAYER_PLACED, true));
            world.setBlock(blockPos, state.setValue(PART, SarcophagusPart.HEAD).setValue(PLAYER_PLACED, true), 3);
            world.updateNeighborsAt(pos, Blocks.AIR, null);
            state.updateNeighbourShapes(world, pos, 3);
        }

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
    public long getSeed(BlockState state, BlockPos pos) {
        BlockPos blockPos = pos.relative(state.getValue(FACING), state.getValue(PART) == SarcophagusPart.HEAD ? 0 : 1);
        return Mth.getSeed(blockPos.getX(), pos.getY(), blockPos.getZ());
    }

    public static DoubleBlockCombiner.BlockType getSarcophagusPart(BlockState state) {
        SarcophagusPart bedPart = state.getValue(PART);
        return bedPart == SarcophagusPart.HEAD ? DoubleBlockCombiner.BlockType.FIRST : DoubleBlockCombiner.BlockType.SECOND;
    }

    @Override
    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        if (!world.isClientSide() && player.isCreative()) {
            SarcophagusPart part = state.getValue(PART);

            if (part == SarcophagusPart.FOOT) {
                BlockPos blockPos = pos.relative(getDirectionTowardsOtherPart(part, state.getValue(FACING)));
                BlockState blockState = world.getBlockState(blockPos);

                if (blockState.is(this) && blockState.getValue(PART) == SarcophagusPart.HEAD) {
                    world.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 35);
                    world.levelEvent(player, 2001, blockPos, Block.getId(blockState));
                }
            }
        }

        return super.playerWillDestroy(world, pos, state, player);
    }


    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel world, BlockPos pos, boolean moved) {
        Containers.updateNeighboursAfterDestroy(state, world, pos);
    }


    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, net.minecraft.util.RandomSource random) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof SarcophagusBlockEntity) {
            ((SarcophagusBlockEntity) blockEntity).onScheduledTick();
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SarcophagusBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        FluidState fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());
        Direction direction = ctx.getHorizontalDirection();
        BlockPos blockPos = ctx.getClickedPos();
        BlockPos blockPos2 = blockPos.relative(direction);
        Level world = ctx.getLevel();
        return world.getBlockState(blockPos2).canBeReplaced(ctx) && world.getWorldBorder().isWithinBounds(blockPos2) ? this.defaultBlockState()
                .setValue(FACING, direction)
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER) : null;
    }

    @Override
    public boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    public static Direction getOppositePartDirection(BlockState state) {
        Direction direction = state.getValue(FACING);
        return state.getValue(PART) == SarcophagusPart.HEAD ? direction.getOpposite() : direction;
    }

    public static DoubleBlockCombiner.Combiner<SarcophagusBlockEntity, Float2FloatFunction> getAnimationProgressRetriever(
            LidBlockEntity progress
    ) {
        return new DoubleBlockCombiner.Combiner<>() {
            @Override
            public Float2FloatFunction acceptDouble(
                    SarcophagusBlockEntity chestBlockEntity,
                    SarcophagusBlockEntity chestBlockEntity2
            ) {
                return (tickDelta) -> Math.max(chestBlockEntity.getOpenNess(tickDelta), chestBlockEntity2.getOpenNess(tickDelta));
            }

            @Override
            public Float2FloatFunction acceptSingle(SarcophagusBlockEntity chestBlockEntity) {
                Objects.requireNonNull(chestBlockEntity);
                return chestBlockEntity::getOpenNess;
            }

            @Override
            public Float2FloatFunction acceptNone() {
                Objects.requireNonNull(progress);
                return progress::getOpenNess;
            }
        };
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level world,
            BlockState state,
            BlockEntityType<T> type
    ) {
        return world.isClientSide() ? createTickerHelper(type, TGBlockEntities.SARCOPHAGUS_BLOCK_ENTITY.get(), SarcophagusBlockEntity::clientTick) : null;
    }

    public Supplier<Item> getLid() {
        return this.lid;
    }

    public Supplier<Item> getBase() {
        return this.base;
    }


    static {
        PLAYER_PLACED = BlockStateProperties.LOCKED;
        OPEN = BlockStateProperties.OPEN;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        FACING = BlockStateProperties.HORIZONTAL_FACING;
        DOUBLE_NORTH_SHAPE = Block.box(1.0D, 1.0D, 1.0D, 15.0D, 14.0D, 15.0D);
        DOUBLE_SOUTH_SHAPE = Block.box(1.0D, 1.0D, 1.0D, 15.0D, 14.0D, 15.0D);
        DOUBLE_WEST_SHAPE = Block.box(1.0D, 1.0D, 1.0D, 15.0D, 14.0D, 15.0D);
        DOUBLE_EAST_SHAPE = Block.box(1.0D, 1.0D, 1.0D, 15.0D, 14.0D, 15.0D);
    }

}
