package com.finallion.graveyard.blocks;

import com.finallion.graveyard.blockentities.SarcophagusBlockEntity;
import com.finallion.graveyard.blockentities.enums.SarcophagusPart;
import com.finallion.graveyard.init.TGBlocks;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.block.ChestAnimationProgress;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.*;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
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

public class SarcophagusBlock extends AbstractCoffinBlock<SarcophagusBlockEntity> implements Waterloggable, BlockEntityProvider {
    public static final BooleanProperty WATERLOGGED;
    public static final BooleanProperty OPEN;
    public static final DirectionProperty FACING;
    protected static final VoxelShape DOUBLE_NORTH_SHAPE;
    protected static final VoxelShape DOUBLE_SOUTH_SHAPE;
    protected static final VoxelShape DOUBLE_WEST_SHAPE;
    protected static final VoxelShape DOUBLE_EAST_SHAPE;
    public static final EnumProperty<SarcophagusPart> PART = EnumProperty.of("part", SarcophagusPart.class);

    public SarcophagusBlock() {
        super(FabricBlockSettings.of(Material.STONE).nonOpaque().strength(1.5F), () -> TGBlocks.SARCOPHAGUS_BLOCK_ENTITY);
        this.setDefaultState(this.stateManager.getDefaultState().with(WATERLOGGED, false).with(OPEN, false).with(PART, SarcophagusPart.FOOT));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, OPEN, FACING, PART);
    }


    @Nullable
    public static Direction getDirection(BlockView world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        return blockState.getBlock() instanceof SarcophagusBlock ? (Direction) blockState.get(FACING) : null;
    }

    public FluidState getFluidState(BlockState state) {
        return (Boolean) state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction == getDirectionTowardsOtherPart((SarcophagusPart) state.get(PART), (Direction) state.get(FACING))) {
            return neighborState.isOf(this) && neighborState.get(PART) != state.get(PART) ? (BlockState) state : Blocks.AIR.getDefaultState();
        } else {
            return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
        }
    }

    private static Direction getDirectionTowardsOtherPart(SarcophagusPart part, Direction direction) {
        return part == SarcophagusPart.FOOT ? direction : direction.getOpposite();
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (state.get(FACING)) {
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


    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            // if part is head, offset to the blockentity of the foot
            if (state.get(PART) == SarcophagusPart.HEAD) {
                pos = pos.offset((Direction) state.get(FACING).getOpposite());
            }

            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof SarcophagusBlockEntity) {
                player.openHandledScreen((SarcophagusBlockEntity) blockEntity);
            }
            return ActionResult.CONSUME;
        }

    }

    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (!world.isClient) {
            BlockPos blockPos = pos.offset((Direction) state.get(FACING));
            world.setBlockState(blockPos, (BlockState) state.with(PART, SarcophagusPart.HEAD), 3);
            world.updateNeighbors(pos, Blocks.AIR);
            state.updateNeighbors(world, pos, 3);
        }

    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.get(FACING)));
    }

    public long getRenderingSeed(BlockState state, BlockPos pos) {
        BlockPos blockPos = pos.offset((Direction) state.get(FACING), state.get(PART) == SarcophagusPart.HEAD ? 0 : 1);
        return MathHelper.hashCode(blockPos.getX(), pos.getY(), blockPos.getZ());
    }

    public static DoubleBlockProperties.Type getSarcophagusPart(BlockState state) {
        SarcophagusPart bedPart = (SarcophagusPart) state.get(PART);
        return bedPart == SarcophagusPart.HEAD ? DoubleBlockProperties.Type.FIRST : DoubleBlockProperties.Type.SECOND;
    }

    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient && player.isCreative()) {
            SarcophagusPart part = (SarcophagusPart) state.get(PART);
            if (part == SarcophagusPart.FOOT) {
                BlockPos blockPos = pos.offset(getDirectionTowardsOtherPart(part, (Direction) state.get(FACING)));
                BlockState blockState = world.getBlockState(blockPos);
                if (blockState.isOf(this) && blockState.get(PART) == SarcophagusPart.HEAD) {
                    world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 35);
                    world.syncWorldEvent(player, 2001, blockPos, Block.getRawIdFromState(blockState));
                }
            }
        }

        super.onBreak(world, pos, state, player);
    }


    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof Inventory) {
                ItemScatterer.spawn(world, pos, (Inventory) blockEntity);
                world.updateComparators(pos, this);
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof SarcophagusBlockEntity) {
            ((SarcophagusBlockEntity) blockEntity).onScheduledTick();
        }
    }

    @Nullable
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SarcophagusBlockEntity(pos, state);
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        Direction direction = ctx.getPlayerFacing();
        BlockPos blockPos = ctx.getBlockPos();
        BlockPos blockPos2 = blockPos.offset(direction);
        World world = ctx.getWorld();
        return world.getBlockState(blockPos2).canReplace(ctx) && world.getWorldBorder().contains(blockPos2) ? (BlockState) this.getDefaultState().with(FACING, direction).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER) : null;
    }

    public static Direction getOppositePartDirection(BlockState state) {
        Direction direction = (Direction) state.get(FACING);
        return state.get(PART) == SarcophagusPart.HEAD ? direction.getOpposite() : direction;
    }

    /*
    ANIMATION STUFF
     */

    public static DoubleBlockProperties.PropertyRetriever<SarcophagusBlockEntity, Float2FloatFunction> getAnimationProgressRetriever(ChestAnimationProgress progress) {
        return new DoubleBlockProperties.PropertyRetriever<SarcophagusBlockEntity, Float2FloatFunction>() {
            public Float2FloatFunction getFromBoth(SarcophagusBlockEntity chestBlockEntity, SarcophagusBlockEntity chestBlockEntity2) {
                return (tickDelta) -> {
                    return Math.max(chestBlockEntity.getAnimationProgress(tickDelta), chestBlockEntity2.getAnimationProgress(tickDelta));
                };
            }

            public Float2FloatFunction getFrom(SarcophagusBlockEntity chestBlockEntity) {
                Objects.requireNonNull(chestBlockEntity);
                return chestBlockEntity::getAnimationProgress;
            }

            public Float2FloatFunction getFallback() {
                ChestAnimationProgress var10000 = progress;
                Objects.requireNonNull(var10000);
                return var10000::getAnimationProgress;
            }
        };
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? checkType(type, this.getExpectedEntityType(), SarcophagusBlockEntity::clientTick) : null;
    }


    @Override
    public DoubleBlockProperties.PropertySource<? extends SarcophagusBlockEntity> getBlockEntitySource(BlockState state, World world, BlockPos pos, boolean ignoreBlocked) {
        BiPredicate biPredicate;
        biPredicate = (worldx, posx) -> {
            return false;
        };

        return DoubleBlockProperties.toPropertySource((BlockEntityType)this.entityTypeRetriever.get(), SarcophagusBlock::getSarcophagusPart, SarcophagusBlock::getOppositePartDirection, FACING, state, world, pos, biPredicate);
    }


    public BlockEntityType<? extends SarcophagusBlockEntity> getExpectedEntityType() {
        return (BlockEntityType)this.entityTypeRetriever.get();
    }




    static {
        OPEN = Properties.OPEN;
        WATERLOGGED = Properties.WATERLOGGED;
        FACING = Properties.HORIZONTAL_FACING;
        DOUBLE_NORTH_SHAPE = Block.createCuboidShape(1.0D, 1.0D, 1.0D, 15.0D, 14.0D, 15.0D);
        DOUBLE_SOUTH_SHAPE = Block.createCuboidShape(1.0D, 1.0D, 1.0D, 15.0D, 14.0D, 15.0D);
        DOUBLE_WEST_SHAPE = Block.createCuboidShape(1.0D, 1.0D, 1.0D, 15.0D, 14.0D, 15.0D);
        DOUBLE_EAST_SHAPE = Block.createCuboidShape(1.0D, 1.0D, 1.0D, 15.0D, 14.0D, 15.0D);
    }

}
