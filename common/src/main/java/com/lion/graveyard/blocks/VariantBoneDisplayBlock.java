package com.lion.graveyard.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

/// Client and server derive the variant from the block position.
public class VariantBoneDisplayBlock extends BoneDisplayBlock {
    public static final IntegerProperty VARIANT = IntegerProperty.create("variant", 0, 3);

    public VariantBoneDisplayBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(VARIANT, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(VARIANT);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState state = super.getStateForPlacement(ctx);
        BlockPos pos = ctx.getClickedPos();
        int variant = Math.floorMod(Mth.getSeed(pos.getX(), pos.getY(), pos.getZ()), 4);
        return state == null ? null : state.setValue(VARIANT, variant);
    }

    @Override
    protected InteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level world,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        if (hand != InteractionHand.MAIN_HAND || !stack.isEmpty() || !player.mayBuild()) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        if (!world.isClientSide()) {
            int next = (state.getValue(VARIANT) + 1) % 4;
            world.setBlock(pos, state.setValue(VARIANT, next), Block.UPDATE_ALL);
        }

        return InteractionResult.SUCCESS;
    }
}
