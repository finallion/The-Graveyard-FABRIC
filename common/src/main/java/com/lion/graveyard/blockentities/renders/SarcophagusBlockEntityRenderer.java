package com.lion.graveyard.blockentities.renders;

import com.lion.graveyard.blockentities.SarcophagusBlockEntity;
import com.lion.graveyard.blockentities.enums.SarcophagusPart;
import com.lion.graveyard.blocks.SarcophagusBlock;
import com.lion.graveyard.init.TGBlockEntities;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LidOpenable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.util.math.Direction;

import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public class SarcophagusBlockEntityRenderer<T extends BlockEntity & LidOpenable> implements BlockEntityRenderer<SarcophagusBlockEntity> {
    private MinecraftClient client;

    public SarcophagusBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.client = MinecraftClient.getInstance();
    }

    @Override
    public int getRenderDistance() {
        return 45;
    }

    @Override
    public void render(SarcophagusBlockEntity entity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        BlockState blockState = entity.getCachedState();
        DoubleBlockProperties.PropertySource<? extends SarcophagusBlockEntity> propertySource = DoubleBlockProperties.toPropertySource(TGBlockEntities.SARCOPHAGUS_BLOCK_ENTITY.get(), SarcophagusBlock::getSarcophagusPart, SarcophagusBlock::getOppositePartDirection, ChestBlock.FACING, blockState, entity.getWorld(), entity.getPos(), (worldx, pos) -> false);
        float g = propertySource.apply(SarcophagusBlock.getAnimationProgressRetriever(entity)).get(tickDelta);
        g = 1.0F - g;
        g = 1.0F - g * g * g;

        BakedModel base = getModel(((SarcophagusBlock)blockState.getBlock()).getBase());
        BakedModel lid = getModel(((SarcophagusBlock)blockState.getBlock()).getLid());

        if (entity.getWorld() != null && entity.getCachedState().get(SarcophagusBlock.PART) == SarcophagusPart.HEAD) {
            render(entity, matrixStack, vertexConsumers, light, overlay, g, lid, true);
            render(entity, matrixStack, vertexConsumers, light, overlay, g, base, false);
        }
    }



    private void render(SarcophagusBlockEntity entity, MatrixStack matrixStack, VertexConsumerProvider vertexConsumer, int light, int overlay, float g, BakedModel model, boolean isLid) {
        matrixStack.push();

        Direction direction = entity.getCachedState().get(SarcophagusBlock.FACING).getOpposite();
        float f = direction.asRotation();
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-f));

        switch (direction) {
            case EAST -> matrixStack.translate(-1.0F, 0F, 1.0F);
            case SOUTH -> matrixStack.translate(0, 0F, 1.0F);
            case NORTH -> matrixStack.translate(-1.0F, 0F, 0F);
        }

        if (isLid) {
            matrixStack.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(g * 45)); // lid rotation, originally 70
            matrixStack.translate(g * 0.3, g * 0.3, 0.0F); // moves lid away from body
        }

        client.getBlockRenderManager().getModelRenderer().render(matrixStack.peek(), vertexConsumer.getBuffer(RenderLayers.getEntityBlockLayer(entity.getCachedState(), false)), entity.getCachedState(), model, 1.0F, 1.0F, 1.0F, light, overlay);

        matrixStack.pop();
    }

    public BakedModel getModel(Item item) {
        return client.getItemRenderer().getModel(item.getDefaultStack(), null, null, 0);
    }
}


