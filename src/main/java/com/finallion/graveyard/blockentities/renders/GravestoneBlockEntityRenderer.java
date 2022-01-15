package com.finallion.graveyard.blockentities.renders;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.blockentities.GravestoneBlockEntity;
import com.finallion.graveyard.blocks.GravestoneBlock;
import com.finallion.graveyard.init.TGBlocks;
import com.google.common.collect.Maps;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.SignType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

import java.util.HashMap;
import java.util.List;

@Environment(EnvType.CLIENT)
public class GravestoneBlockEntityRenderer implements BlockEntityRenderer<GravestoneBlockEntity> {
    private static final int RENDER_DISTANCE = MathHelper.square(16);
    private final TextRenderer textRenderer;
    private static final HashMap<Block, RenderLayer> LAYERS = Maps.newHashMap();
    private static RenderLayer defaultLayer;

    private static ItemStack stack = new ItemStack(TGBlocks.GRAVESTONE.asItem(), 1);

    public GravestoneBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.textRenderer = ctx.getTextRenderer();
    }

    public void render(GravestoneBlockEntity signBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        BlockState blockState = signBlockEntity.getCachedState();
        matrixStack.push();

        // text render location in world
        // offset on block
        matrixStack.translate(0.5D, 0.25D, 0.5D);

        float rotation = -blockState.get(GravestoneBlock.FACING).asRotation();
        //float h = -((float)((Integer)blockState.getStructure(SignBlock.ROTATION) * 360) / 16.0F);
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(rotation));
        matrixStack.push();
        // size
        matrixStack.scale(0.6666667F, -0.6666667F, -0.6666667F);
        matrixStack.pop();
        matrixStack.translate(0.0D, 0.3333333432674408D, 0.23);
        matrixStack.scale(0.010416667F, -0.010416667F, 0.010416667F);


        int m = getColor(signBlockEntity);

        OrderedText[] orderedTexts = signBlockEntity.updateSign(MinecraftClient.getInstance().shouldFilterText(), (text) -> {
            List<OrderedText> list = this.textRenderer.wrapLines(text, 90);
            return list.isEmpty() ? OrderedText.EMPTY : (OrderedText)list.get(0);
        });


        int q;
        boolean bl2;
        int r;
        if (signBlockEntity.isGlowingText()) {
            q = signBlockEntity.getTextColor().getSignColor();
            bl2 = shouldRender(signBlockEntity, q);
            r = 15728880;
        } else {
            q = m;
            bl2 = false;
            r = i;
        }


        for(int s = 0; s < 4; ++s) {
            OrderedText orderedText = orderedTexts[s];
            float t = (float)(-this.textRenderer.getWidth(orderedText) / 2);
            if (bl2) {
                this.textRenderer.drawWithOutline(orderedText, t, (float)(s * 10 - 20), q, m, matrixStack.peek().getPositionMatrix(), vertexConsumerProvider, r);
            } else {
                this.textRenderer.draw(orderedText, t, (float)(s * 10 - 20), q, false, matrixStack.peek().getPositionMatrix(), vertexConsumerProvider, false, 0, r);
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
        //MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.GROUND, i, j, matrixStack, vertexConsumerProvider, 2);
        MinecraftClient.getInstance().getItemRenderer().renderItem(new ItemStack(state.getBlock().asItem(), 1), ModelTransformation.Mode.GROUND, i, j, matrixStack, vertexConsumerProvider, 2);
        matrixStack.pop();
    }



    private static boolean shouldRender(GravestoneBlockEntity sign, int signColor) {
        if (signColor == DyeColor.BLACK.getSignColor()) {
            return true;
        } else {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            ClientPlayerEntity clientPlayerEntity = minecraftClient.player;
            if (clientPlayerEntity != null && minecraftClient.options.getPerspective().isFirstPerson() && clientPlayerEntity.isUsingSpyglass()) {
                return true;
            } else {
                Entity entity = minecraftClient.getCameraEntity();
                return entity != null && entity.squaredDistanceTo(Vec3d.ofCenter(sign.getPos())) < (double)RENDER_DISTANCE;
            }
        }
    }

    private static int getColor(GravestoneBlockEntity sign) {
        int i = sign.getTextColor().getSignColor();
        double d = 0.4D;
        int j = (int)((double) NativeImage.getRed(i) * 0.4D);
        int k = (int)((double)NativeImage.getGreen(i) * 0.4D);
        int l = (int)((double)NativeImage.getBlue(i) * 0.4D);
        return i == DyeColor.BLACK.getSignColor() && sign.isGlowingText() ? -988212 : NativeImage.packColor(0, l, k, j);
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

    public static SignBlockEntityRenderer.SignModel createSignModel(EntityModelLoader entityModelLoader, SignType type) {
        return new SignBlockEntityRenderer.SignModel(entityModelLoader.getModelPart(EntityModelLayers.createSign(type)));
    }


    static {
        defaultLayer = RenderLayer.getEntitySolid(new Identifier("textures/entity/signs/oak.png"));
        RenderLayer layer1 = RenderLayer.getEntitySolid(new Identifier(TheGraveyard.MOD_ID, "textures/entity/gravestone/polished_basalt.png"));
        RenderLayer layer2 = RenderLayer.getEntitySolid(new Identifier(TheGraveyard.MOD_ID, "textures/entity/gravestone/cobblestone.png"));
        RenderLayer layer3 = RenderLayer.getEntitySolid(new Identifier(TheGraveyard.MOD_ID, "textures/entity/gravestone/mossy_cobblestone.png"));
        RenderLayer layer4 = RenderLayer.getEntitySolid(new Identifier(TheGraveyard.MOD_ID, "textures/entity/gravestone/deepslate.png"));
        LAYERS.put(TGBlocks.GRAVESTONE, layer1);
        LAYERS.put(TGBlocks.COBBLESTONE_GRAVESTONE, layer2);
        LAYERS.put(TGBlocks.MOSSY_COBBLESTONE_GRAVESTONE, layer3);
        LAYERS.put(TGBlocks.DEEPSLATE_GRAVESTONE, layer4);

    }



    public static VertexConsumer getConsumer(VertexConsumerProvider provider, Block block) {
        return provider.getBuffer(LAYERS.getOrDefault(block, defaultLayer));
    }
}

