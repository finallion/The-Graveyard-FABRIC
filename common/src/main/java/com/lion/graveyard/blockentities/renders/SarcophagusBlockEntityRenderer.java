package com.lion.graveyard.blockentities.renders;

import com.lion.graveyard.blockentities.SarcophagusBlockEntity;
import com.lion.graveyard.blockentities.enums.SarcophagusPart;
import com.lion.graveyard.blocks.SarcophagusBlock;
import com.lion.graveyard.init.TGBlockEntities;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public class SarcophagusBlockEntityRenderer<T extends BlockEntity & LidBlockEntity> implements BlockEntityRenderer<SarcophagusBlockEntity> {
    private final ItemModelResolver itemModelResolver;
    private final ItemStackRenderState baseState = new ItemStackRenderState();
    private final ItemStackRenderState lidState = new ItemStackRenderState();

    public SarcophagusBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.itemModelResolver = ctx.getItemModelResolver();
    }

    @Override
    public int getViewDistance() {
        return 45;
    }

    @Override
    public void render(
            SarcophagusBlockEntity entity,
            float tickDelta,
            PoseStack matrixStack,
            MultiBufferSource vertexConsumers,
            int light,
            int overlay,
            Vec3 cameraPosition
    ) {
        BlockState blockState = entity.getBlockState();
        DoubleBlockCombiner.NeighborCombineResult<? extends SarcophagusBlockEntity> propertySource = DoubleBlockCombiner.combineWithNeigbour(TGBlockEntities.SARCOPHAGUS_BLOCK_ENTITY.get(), SarcophagusBlock::getSarcophagusPart, SarcophagusBlock::getOppositePartDirection, ChestBlock.FACING, blockState, entity.getLevel(), entity.getBlockPos(), (worldx, pos) -> false);
        float g = propertySource.apply(SarcophagusBlock.getAnimationProgressRetriever(entity)).get(tickDelta);
        g = 1.0F - g;
        g = 1.0F - g * g * g;

        ItemStackRenderState base = getModel(this.baseState, ((SarcophagusBlock)blockState.getBlock()).getBase().get(), entity);
        ItemStackRenderState lid = getModel(this.lidState, ((SarcophagusBlock)blockState.getBlock()).getLid().get(), entity);

        if (entity.getLevel() != null && entity.getBlockState().getValue(SarcophagusBlock.PART) == SarcophagusPart.HEAD) {
            render(entity, matrixStack, vertexConsumers, light, overlay, g, lid, true);
            render(entity, matrixStack, vertexConsumers, light, overlay, g, base, false);
        }
    }



    private void render(
            SarcophagusBlockEntity entity,
            PoseStack matrixStack,
            MultiBufferSource vertexConsumer,
            int light,
            int overlay,
            float g,
            ItemStackRenderState model,
            boolean isLid
    ) {
        matrixStack.pushPose();

        Direction direction = entity.getBlockState().getValue(SarcophagusBlock.FACING).getOpposite();
        float f = direction.toYRot();
        matrixStack.mulPose(Axis.YP.rotationDegrees(-f));

        switch (direction) {
            case EAST -> matrixStack.translate(-1.0F, 0F, 1.0F);
            case SOUTH -> matrixStack.translate(0, 0F, 1.0F);
            case NORTH -> matrixStack.translate(-1.0F, 0F, 0F);
        }

        if (isLid) {
            matrixStack.mulPose(Axis.ZN.rotationDegrees(g * 45));
            matrixStack.translate(g * 0.3, g * 0.3, 0.0F);
        }

        // Cancels the -0.5 offset that ItemTransform.apply adds.
        matrixStack.translate(0.5F, 0.5F, 0.5F);
        model.render(matrixStack, vertexConsumer, light, overlay);

        matrixStack.popPose();
    }

    private ItemStackRenderState getModel(ItemStackRenderState state, Item item, SarcophagusBlockEntity entity) {
        state.clear();
        this.itemModelResolver.updateForTopItem(state, item.getDefaultInstance(), ItemDisplayContext.NONE, entity.getLevel(), null, 0);
        return state;
    }
}


