package com.lion.graveyard.blocks;

import com.lion.graveyard.blockentities.SarcophagusBlockEntity;
import com.lion.graveyard.blockentities.enums.SarcophagusPart;
import com.lion.graveyard.entities.WraithEntity;
import com.lion.graveyard.init.*;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
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
import java.util.function.BiPredicate;
import java.util.function.Supplier;

/*
THINGS TO CHECK IF THE MODEL CASTS UNWANTED SHADOWS:
- Is the hitbox smaller than a block? (!!!)
- It "ambientocclusion:false" set in the model json (see beacon model)?
- "shade:false" under parts in the model json might help (see torch model).
- Is the model renderer set up correctly? Especially the light value (look at chest/bed)?
- Make your block cast light.
- Methods: translucent and ambient occlusion from abstract block class might help.
- Set model sizes from 16 to 15.9 where shadow occurs.
- renderFlat in model renderer might help.
 */

public class SarcophagusBlock extends BaseEntityBlock implements SimpleWaterloggedBlock, EntityBlock {
    public static final BooleanProperty WATERLOGGED;
    public static final BooleanProperty OPEN;
    public static final BooleanProperty PLAYER_PLACED;
    public static final BooleanProperty IS_COFFIN = BooleanProperty.create("is_coffin");
    public static final DirectionProperty FACING;
    protected static final VoxelShape DOUBLE_NORTH_SHAPE;
    protected static final VoxelShape DOUBLE_SOUTH_SHAPE;
    protected static final VoxelShape DOUBLE_WEST_SHAPE;
    protected static final VoxelShape DOUBLE_EAST_SHAPE;
    public static final EnumProperty<SarcophagusPart> PART = EnumProperty.create("part", SarcophagusPart.class);
    private final Supplier<Item> lid;
    private final Supplier<Item> base;
    public SarcophagusBlock(BlockBehaviour.Properties settings, boolean isCoffin, Supplier<Item> lid, Supplier<Item> base) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false).setValue(OPEN, false).setValue(PART, SarcophagusPart.FOOT).setValue(PLAYER_PLACED, false).setValue(IS_COFFIN, isCoffin));
        this.base = base;
        this.lid = lid;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_51562_) {
        p_51562_.add(WATERLOGGED, OPEN, FACING, PART, PLAYER_PLACED, IS_COFFIN);
    }

    public FluidState getFluidState(BlockState p_51581_) {
        return p_51581_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_51581_);
    }

    public BlockState updateShape(BlockState p_49525_, Direction p_49526_, BlockState p_49527_, LevelAccessor p_49528_, BlockPos p_49529_, BlockPos p_49530_) {
        if (p_49526_ == getNeighbourDirection(p_49525_.getValue(PART), p_49525_.getValue(FACING))) {
            return p_49527_.is(this) && p_49527_.getValue(PART) != p_49525_.getValue(PART) ? p_49525_ : Blocks.AIR.defaultBlockState();
        } else {
            return super.updateShape(p_49525_, p_49526_, p_49527_, p_49528_, p_49529_, p_49530_);
        }
    }

    private static Direction getNeighbourDirection(SarcophagusPart p_49534_, Direction p_49535_) {
        return p_49534_ == SarcophagusPart.FOOT ? p_49535_ : p_49535_.getOpposite();
    }

    private static Direction getDirectionTowardsOtherPart(SarcophagusPart part, Direction direction) {
        return part == SarcophagusPart.FOOT ? direction : direction.getOpposite();
    }

    @Override
    public boolean isPathfindable(BlockState p_60475_, BlockGetter p_60476_, BlockPos p_60477_, PathComputationType p_60478_) {
        return false;
    }

    public VoxelShape getShape(BlockState p_51569_, BlockGetter p_51570_, BlockPos p_51571_, CollisionContext p_51572_) {
        return switch (p_51569_.getValue(FACING)) {
            default -> DOUBLE_NORTH_SHAPE;
            case SOUTH -> DOUBLE_SOUTH_SHAPE;
            case WEST -> DOUBLE_WEST_SHAPE;
            case EAST -> DOUBLE_EAST_SHAPE;
        };
    }

    public InteractionResult use(BlockState p_49515_, Level p_49516_, BlockPos p_49517_, Player p_49518_, InteractionHand p_49519_, BlockHitResult p_49520_) {
        Random random = new Random();

        if (p_49516_.isClientSide) {
            return InteractionResult.CONSUME;
        } else {
            BlockPos original = p_49517_;
            if (p_49515_.getValue(PART) == SarcophagusPart.HEAD) {
                p_49517_ = p_49517_.relative(p_49515_.getValue(FACING).getOpposite());
            }

            MenuProvider menuprovider = this.getMenuProvider(p_49515_, p_49516_, p_49517_);
            if (menuprovider != null) {
                p_49518_.openMenu(menuprovider);
            }

            spawnGhost(p_49515_, p_49516_, original, p_49518_, random);

            return InteractionResult.CONSUME;
        }
    }

    public static void spawnGhost(BlockState state, Level world, BlockPos pos, Player player, Random random) {
        if (!state.getValue(PLAYER_PLACED) && random.nextInt(4) == 0 && pos.getY() < 62) {
            BlockPos entityPos = pos;
            for (int i = 0; i < 10; i++) { // 10 spawn attempts to find air, else just spawn
                entityPos = player.getOnPos().offset(-2 + random.nextInt(5), 1, -2 + random.nextInt(5));
                if (world.getBlockState(entityPos).isAir() && world.getBlockState(entityPos.above()).isAir()) {
                    break;
                }
            }
            WraithEntity ghost = TGEntities.WRAITH.get().create(world);
            ghost.moveTo(entityPos, 0.0F, 0.0F);
            world.addFreshEntity(ghost);
            world.setBlock(pos, state.setValue(PLAYER_PLACED, true), 3);
            BlockPos otherPartPos = pos.relative(getDirectionTowardsOtherPart(state.getValue(PART), state.getValue(FACING)));
            BlockState otherPart = world.getBlockState(otherPartPos);
            if (player instanceof ServerPlayer) {
                TGCriteria.SPAWN_WRAITH.get().trigger((ServerPlayer) player);
            }
            world.setBlock(otherPartPos, otherPart.setValue(PLAYER_PLACED, true), 3);
        }
    }

    public void setPlacedBy(Level p_49499_, BlockPos p_49500_, BlockState p_49501_, LivingEntity p_49502_, ItemStack p_49503_) {
        super.setPlacedBy(p_49499_, p_49500_, p_49501_, p_49502_, p_49503_);
        if (!p_49499_.isClientSide) {
            BlockPos blockpos = p_49500_.relative(p_49501_.getValue(FACING));
            p_49499_.setBlock(p_49500_, p_49501_.setValue(PLAYER_PLACED, true), 3);
            p_49499_.setBlock(blockpos, p_49501_.setValue(PART, SarcophagusPart.HEAD).setValue(PLAYER_PLACED, true), 3);
            p_49499_.blockUpdated(p_49500_, Blocks.AIR);
            p_49501_.updateNeighbourShapes(p_49499_, p_49500_, 3);
        }
    }

    public BlockState rotate(BlockState p_51552_, Rotation p_51553_) {
        return p_51552_.setValue(FACING, p_51553_.rotate(p_51552_.getValue(FACING)));
    }

    public BlockState mirror(BlockState p_54122_, Mirror p_54123_) {
        return p_54122_.rotate(p_54123_.getRotation(p_54122_.getValue(FACING)));
    }

    public long getSeed(BlockState p_49522_, BlockPos p_49523_) {
        BlockPos blockpos = p_49523_.relative(p_49522_.getValue(FACING), p_49522_.getValue(PART) == SarcophagusPart.HEAD ? 0 : 1);
        return Mth.getSeed(blockpos.getX(), p_49523_.getY(), blockpos.getZ());
    }


    public static DoubleBlockCombiner.BlockType getBlockType(BlockState state) {
        SarcophagusPart bedPart = (SarcophagusPart) state.getValue(PART);
        return bedPart == SarcophagusPart.HEAD ? DoubleBlockCombiner.BlockType.FIRST : DoubleBlockCombiner.BlockType.SECOND;
    }

    public BlockState playerWillDestroy(Level p_49505_, BlockPos p_49506_, BlockState p_49507_, Player p_49508_) {
        if (!p_49505_.isClientSide && p_49508_.isCreative()) {
            SarcophagusPart part = p_49507_.getValue(PART);
            if (part == SarcophagusPart.FOOT) {
                BlockPos blockpos = p_49506_.relative(getNeighbourDirection(part, p_49507_.getValue(FACING)));
                BlockState blockstate = p_49505_.getBlockState(blockpos);
                if (blockstate.is(this) && blockstate.getValue(PART) == SarcophagusPart.HEAD) {
                    p_49505_.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
                    p_49505_.levelEvent(p_49508_, 2001, blockpos, Block.getId(blockstate));
                }
            }
        }

        return super.playerWillDestroy(p_49505_, p_49506_, p_49507_, p_49508_);
    }

    public void onRemove(BlockState p_51538_, Level p_51539_, BlockPos p_51540_, BlockState p_51541_, boolean p_51542_) {
        if (!p_51538_.is(p_51541_.getBlock())) {
            BlockEntity blockentity = p_51539_.getBlockEntity(p_51540_);
            if (blockentity instanceof Container) {
                Containers.dropContents(p_51539_, p_51540_, (Container)blockentity);
                p_51539_.updateNeighbourForOutputSignal(p_51540_, this);
            }

            super.onRemove(p_51538_, p_51539_, p_51540_, p_51541_, p_51542_);
        }
    }

    public void tick(BlockState p_153059_, ServerLevel p_153060_, BlockPos p_153061_, Random p_153062_) {
        BlockEntity blockentity = p_153060_.getBlockEntity(p_153061_);
        if (blockentity instanceof SarcophagusBlockEntity) {
            ((SarcophagusBlockEntity)blockentity).onScheduledTick();
        }

    }

    public BlockEntity newBlockEntity(BlockPos p_153064_, BlockState p_153065_) {
        return new SarcophagusBlockEntity(p_153064_, p_153065_);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    public RenderShape getRenderShape(BlockState p_51567_) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext p_49479_) {
        FluidState fluidstate = p_49479_.getLevel().getFluidState(p_49479_.getClickedPos());
        Direction direction = p_49479_.getHorizontalDirection();
        BlockPos blockpos = p_49479_.getClickedPos();
        BlockPos blockpos1 = blockpos.relative(direction);
        Level level = p_49479_.getLevel();
        return level.getBlockState(blockpos1).canBeReplaced(p_49479_) && level.getWorldBorder().isWithinBounds(blockpos1) ? this.defaultBlockState().setValue(FACING, direction).setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER)) : null;
    }

    public static Direction getConnectedDirection(BlockState p_51585_) {
        Direction direction = p_51585_.getValue(FACING);
        return p_51585_.getValue(PART) == SarcophagusPart.HEAD ? direction.getOpposite() : direction;
    }

    // ANIMATION STUFF


    public static DoubleBlockCombiner.Combiner<SarcophagusBlockEntity, Float2FloatFunction> getAnimationProgressRetriever(LidBlockEntity progress) {
        return new DoubleBlockCombiner.Combiner<>() {
            public Float2FloatFunction acceptDouble(SarcophagusBlockEntity chestBlockEntity, SarcophagusBlockEntity chestBlockEntity2) {
                return (tickDelta) -> Math.max(chestBlockEntity.getOpenNess(tickDelta), chestBlockEntity2.getOpenNess(tickDelta));
            }

            public Float2FloatFunction acceptSingle(SarcophagusBlockEntity chestBlockEntity) {
                Objects.requireNonNull(chestBlockEntity);
                return chestBlockEntity::getOpenNess;
            }

            public Float2FloatFunction acceptNone() {
                return progress::getOpenNess;
            }
        };
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153055_, BlockState p_153056_, BlockEntityType<T> p_153057_) {
        return p_153055_.isClientSide ? createTickerHelper(p_153057_, TGBlockEntities.SARCOPHAGUS_BLOCK_ENTITY.get(), SarcophagusBlockEntity::clientTick) : null;
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
