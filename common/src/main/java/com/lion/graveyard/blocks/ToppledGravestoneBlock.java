package com.lion.graveyard.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

/// A gravestone that has fallen over and lies on its back. Same material and same inscription as the
/// standing one - only the inscription now faces the sky, which is what the pitch is for.
public class ToppledGravestoneBlock extends CarvedGravestoneBlock {

    private static final VoxelShape SHAPE_FACING_NS = Block.createCuboidShape(1.0D, 0.0D, 0.0D, 15.0D, 6.0D, 16.0D);
    private static final VoxelShape SHAPE_FACING_EW = Block.createCuboidShape(0.0D, 0.0D, 1.0D, 16.0D, 6.0D, 15.0D);

    public ToppledGravestoneBlock(Identifier texture) {
        // Just clear of the stone's upward face, which is six pixels up.
        super(texture, false, 0.15D, 0.06D, -90.0F);
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
