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

    public SarcophagusBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {}

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

        Item baseItem = ((SarcophagusBlock)blockState.getBlock()).getBase();
        BakedModel base = getModel(baseItem);

        Item lidItem = ((SarcophagusBlock)blockState.getBlock()).getLid();
        BakedModel lid = getModel(lidItem);

        if (entity.getWorld() != null && entity.getCachedState().get(SarcophagusBlock.PART) == SarcophagusPart.HEAD) {
            Random random = entity.getWorld().random;
            render(entity, matrixStack, vertexConsumers, light, g, random, lid, true);
            render(entity, matrixStack, vertexConsumers, light, g, random, base, false);
        }
    }



    private void render(SarcophagusBlockEntity entity, MatrixStack matrixStack, VertexConsumerProvider vertexConsumer, int light, float g, Random random, BakedModel model, boolean isLid) {
        matrixStack.push();

        VertexConsumer vertices = vertexConsumer.getBuffer(RenderLayers.getEntityBlockLayer(entity.getCachedState(), false));
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
            //matrices.translate(isFoot ? -(openFactor * 0.25) : openFactor * 0.25, openFactor * 0.25, 0.0F);
            matrixStack.translate(g * 0.3, g * 0.3, 0.0F); // moves lid away from body
        }

        for (int i = 0; i <= 6; i++) {
            for (BakedQuad q : model.getQuads(null, ModelHelper.faceFromIndex(i), random)) {
                vertices.quad(matrixStack.peek(), q, 1, 1, 1, light, OverlayTexture.DEFAULT_UV);
            }
        }

        matrixStack.pop();
    }

    public static BakedModel getModel(Item item) {
        return MinecraftClient.getInstance().getItemRenderer().getModel(item.getDefaultStack(), null, null, 0);
    }
}


