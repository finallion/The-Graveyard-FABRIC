package com.lion.graveyard.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

/// Client and server derive the variant from the block position.
public class VariantBoneDisplayBlock extends BoneDisplayBlock {
    public static final IntProperty VARIANT = IntProperty.of("variant", 0, 3);

    public VariantBoneDisplayBlock() {
        super();
        this.setDefaultState(this.getDefaultState().with(VARIANT, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(VARIANT);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = super.getPlacementState(ctx);
        BlockPos pos = ctx.getBlockPos();
        int variant = Math.floorMod(MathHelper.hashCode(pos.getX(), pos.getY(), pos.getZ()), 4);
        return state == null ? null : state.with(VARIANT, variant);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (hand != Hand.MAIN_HAND || !player.getMainHandStack().isEmpty() || !player.canModifyBlocks()) {
            return ActionResult.PASS;
        }

        if (!world.isClient) {
            int next = (state.get(VARIANT) + 1) % 4;
            world.setBlockState(pos, state.with(VARIANT, next), Block.NOTIFY_ALL);
        }

        return ActionResult.success(world.isClient);
    }
}
