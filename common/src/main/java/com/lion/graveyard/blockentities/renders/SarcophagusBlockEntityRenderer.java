package com.lion.graveyard.blockentities.renders;

import com.lion.graveyard.blockentities.SarcophagusBlockEntity;
import com.lion.graveyard.blockentities.enums.SarcophagusPart;
import com.lion.graveyard.blocks.SarcophagusBlock;
import com.lion.graveyard.init.TGBlockEntities;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Environment(EnvType.CLIENT)
public class SarcophagusBlockEntityRenderer<T extends BlockEntity & LidBlockEntity> implements BlockEntityRenderer<SarcophagusBlockEntity> {
    private Minecraft client;

    public SarcophagusBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.client = Minecraft.getInstance();
    }

    @Override
    public int getViewDistance() {
        return 45;
    }

    @Override
    public void render(SarcophagusBlockEntity entity, float tickDelta, PoseStack matrixStack, MultiBufferSource vertexConsumers, int light, int overlay) {
        BlockState blockState = entity.getBlockState();
        DoubleBlockCombiner.NeighborCombineResult<? extends SarcophagusBlockEntity> propertySource = DoubleBlockCombiner.combineWithNeigbour(TGBlockEntities.SARCOPHAGUS_BLOCK_ENTITY.get(), SarcophagusBlock::getBlockType, SarcophagusBlock::getConnectedDirection, ChestBlock.FACING, blockState, entity.getLevel(), entity.getBlockPos(), (worldx, pos) -> false);
        float g = propertySource.apply(SarcophagusBlock.getAnimationProgressRetriever(entity)).get(tickDelta);
        g = 1.0F - g;
        g = 1.0F - g * g * g;

        BakedModel base = getModel(((SarcophagusBlock)blockState.getBlock()).getBase().get());
        BakedModel lid = getModel(((SarcophagusBlock)blockState.getBlock()).getLid().get());

        if (entity.getLevel() != null && entity.getBlockState().getValue(SarcophagusBlock.PART) == SarcophagusPart.HEAD) {
            render(entity, matrixStack, vertexConsumers, light, overlay, g, lid, true);
            render(entity, matrixStack, vertexConsumers, light, overlay, g, base, false);
        }
    }



    private void render(SarcophagusBlockEntity entity, PoseStack matrixStack, MultiBufferSource vertexConsumer, int light, int overlay, float g, BakedModel model, boolean isLid) {
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
            matrixStack.mulPose(Axis.ZN.rotationDegrees(g * 45)); // lid rotation, originally 70
            matrixStack.translate(g * 0.3, g * 0.3, 0.0F); // moves lid away from body
        }

        client.getBlockRenderer().getModelRenderer().renderModel(matrixStack.last(), vertexConsumer.getBuffer(ItemBlockRenderTypes.getRenderType(entity.getBlockState(), false)), entity.getBlockState(), model, 1.0F, 1.0F, 1.0F, light, overlay);

        matrixStack.popPose();
    }

    public BakedModel getModel(Item item) {
        return client.getItemRenderer().getModel(item.getDefaultInstance(), null, null, 0);
    }
}


