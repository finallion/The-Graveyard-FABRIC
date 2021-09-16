package com.finallion.graveyard.blockentities.render;

import com.finallion.graveyard.blockentities.GravestoneBlockEntity;
import com.finallion.graveyard.blocks.GravestoneBlock;
import com.finallion.graveyard.init.TGBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.SignType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

import java.util.List;

@Environment(EnvType.CLIENT)
public class GravestoneBlockEntityRenderer extends BlockEntityRenderer<GravestoneBlockEntity> {
    private static final float RENDER_DISTANCE = MathHelper.square(16);
    private final TextRenderer textRenderer;

    private static ItemStack stack = new ItemStack(TGBlocks.GRAVESTONE.asItem(), 1);

    public GravestoneBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
        this.textRenderer = blockEntityRenderDispatcher.getTextRenderer();
    }

    public void render(GravestoneBlockEntity signBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        BlockState blockState = signBlockEntity.getCachedState();
        matrixStack.push();


        // text render location in world
        // offset on block
        matrixStack.translate(0.5D, 0.25D, 0.5D);

        float rotation = -blockState.get(GravestoneBlock.FACING).asRotation();
        //float h = -((float)((Integer)blockState.get(SignBlock.ROTATION) * 360) / 16.0F);
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(rotation));
        matrixStack.push();
        // size
        matrixStack.scale(0.6666667F, -0.6666667F, -0.6666667F);
        matrixStack.pop();
        TextRenderer textRenderer = this.dispatcher.getTextRenderer();
        matrixStack.translate(0.0D, 0.3333333432674408D, 0.23);
        matrixStack.scale(0.010416667F, -0.010416667F, 0.010416667F);


        int m = signBlockEntity.getTextColor().getSignColor();
        double d = 0.4D;
        int n = (int)((double)NativeImage.getRed(m) * 0.4D);
        int o = (int)((double)NativeImage.getGreen(m) * 0.4D);
        int p = (int)((double)NativeImage.getBlue(m) * 0.4D);
        int q = NativeImage.getAbgrColor(0, p, o, n);


        for(int s = 0; s < 4; ++s) {
            OrderedText orderedText = signBlockEntity.getTextBeingEditedOnRow(s, (text) -> {
                List<OrderedText> list = textRenderer.wrapLines(text, 90);
                return list.isEmpty() ? OrderedText.EMPTY : (OrderedText)list.get(0);
            });
            if (orderedText != null) {
                float t = (float)(-textRenderer.getWidth(orderedText) / 2);
                textRenderer.draw(orderedText, t, (float)(s * 10 - 20), q, false, matrixStack.peek().getModel(), vertexConsumerProvider, false, 0, i);
            }
        }

        matrixStack.pop();
        renderGrave(blockState, f, matrixStack, vertexConsumerProvider, i, j);

    }

    public void renderGrave(BlockState state, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        matrixStack.push();
        matrixStack.translate(0.5, 0.43, 0.5);
        matrixStack.scale(2.28F, 2.15F, 2.28F);
        float rotation = state.get(GravestoneBlock.FACING).asRotation();
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(rotation));
        MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.GROUND, i, j, matrixStack, vertexConsumerProvider);

        matrixStack.pop();
    }



    private static boolean shouldRender(GravestoneBlockEntity sign, int signColor) {
        if (signColor == DyeColor.BLACK.getSignColor()) {
            return true;
        } else {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            ClientPlayerEntity clientPlayerEntity = minecraftClient.player;
            if (clientPlayerEntity != null && minecraftClient.options.getPerspective().isFirstPerson()) {
                return true;
            } else {
                Entity entity = minecraftClient.getCameraEntity();
                return entity != null && entity.squaredDistanceTo(Vec3d.ofCenter(sign.getPos())) < (double)RENDER_DISTANCE;
            }
        }
    }



    public static SignType getSignType(Block block) {
        SignType signType2;
        if (block instanceof AbstractSignBlock) {
            signType2 = ((AbstractSignBlock)block).getSignType();
        } else {
            signType2 = SignType.CRIMSON;
        }

        return signType2;
    }

    public static SignBlockEntityRenderer.SignModel createSignModel(EntityRenderDispatcher entityModelLoader, SignType type) {
        return new SignBlockEntityRenderer.SignModel();
    }
}

