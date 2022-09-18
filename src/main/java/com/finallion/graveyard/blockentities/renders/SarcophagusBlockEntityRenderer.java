package com.finallion.graveyard.blockentities.renders;

import com.finallion.graveyard.blockentities.SarcophagusBlockEntity;
import com.finallion.graveyard.blockentities.enums.SarcophagusPart;
import com.finallion.graveyard.blocks.SarcophagusBlock;
import com.finallion.graveyard.init.TGBlocks;
import com.finallion.graveyard.init.TGItems;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
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
import net.minecraft.util.math.Vec3f;
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
        DoubleBlockProperties.PropertySource<? extends SarcophagusBlockEntity> propertySource = DoubleBlockProperties.toPropertySource(TGBlocks.SARCOPHAGUS_BLOCK_ENTITY, SarcophagusBlock::getSarcophagusPart, SarcophagusBlock::getOppositePartDirection, ChestBlock.FACING, blockState, entity.getWorld(), entity.getPos(), (worldx, pos) -> {
            return false;
        });
        float g = ((Float2FloatFunction) propertySource.apply(SarcophagusBlock.getAnimationProgressRetriever((LidOpenable) entity))).get(tickDelta);
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
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-f));

        switch (direction) {
            case EAST -> matrixStack.translate(-1.0F, 0F, 1.0F);
            case SOUTH -> matrixStack.translate(0, 0F, 1.0F);
            case NORTH -> matrixStack.translate(-1.0F, 0F, 0F);
        }

        if (isLid) {
            matrixStack.multiply(Vec3f.NEGATIVE_Z.getDegreesQuaternion(g * 70)); // lid rotation
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


