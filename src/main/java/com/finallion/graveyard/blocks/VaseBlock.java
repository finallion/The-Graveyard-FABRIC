package com.finallion.graveyard.blocks;

import com.finallion.graveyard.TheGraveyard;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;

public class VaseBlock extends Block {
    public static final IntProperty VASES;
    public static final BooleanProperty WATERLOGGED;
    private static final VoxelShape VASE_SHAPE_ONE;
    private static final VoxelShape VASE_SHAPE_TWO;
    private static final VoxelShape VASE_SHAPE_THREE;
    private static final VoxelShape VASE_SHAPE_FOUR;



    public VaseBlock() {
        super(FabricBlockSettings.of(Material.GLASS).breakInstantly().noCollision().nonOpaque().sounds(BlockSoundGroup.GLASS).drops(new Identifier(TheGraveyard.MOD_ID, "chests/vase_loot")));
        this.setDefaultState(this.stateManager.getDefaultState().with(VASES, 1).with(WATERLOGGED, false));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, VASES);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos());
        if (blockState.isOf(this)) {
            return (BlockState)blockState.cycle(VASES);
        } else {
            FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
            boolean bl = fluidState.getFluid() == Fluids.WATER;
            return (BlockState)super.getPlacementState(ctx).with(WATERLOGGED, bl);
        }
    }

    public FluidState getFluidState(BlockState state) {
        return (Boolean)state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        return !context.shouldCancelInteraction() && context.getStack().getItem() == this.asItem() && (Integer)state.get(VASES) < 4 ? true : super.canReplace(state, context);
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch((Integer)state.get(VASES)) {
            case 1:
            default:
                return VASE_SHAPE_ONE;
            case 2:
                return VASE_SHAPE_TWO;
            case 3:
                return VASE_SHAPE_THREE;
            case 4:
                return VASE_SHAPE_FOUR;
        }
    }



    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return Block.sideCoversSmallSquare(world, pos.down(), Direction.UP);
    }


    static {
        VASES = Properties.CANDLES;
        WATERLOGGED = Properties.WATERLOGGED;
        VASE_SHAPE_ONE = Block.createCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 12.0D, 12.0D);
        VASE_SHAPE_TWO = Block.createCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 15.0D, 13.0D);
        VASE_SHAPE_THREE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
        VASE_SHAPE_FOUR = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    }
}
