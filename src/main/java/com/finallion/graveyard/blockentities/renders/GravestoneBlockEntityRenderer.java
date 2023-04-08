package com.finallion.graveyard.blockentities.renders;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.blockentities.GravestoneBlockEntity;
import com.finallion.graveyard.blocks.GravestoneBlock;
import com.finallion.graveyard.init.TGBlocks;
import com.google.common.collect.Maps;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.model.Model;
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
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

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
        World world = signBlockEntity.getWorld();
        matrixStack.push();

        // text render location in world
        // offset on block
        matrixStack.translate(0.5D, 0.25D, 0.5D);

        float rotation = -blockState.get(GravestoneBlock.FACING).asRotation();
        //float h = -((float)((Integer)blockState.getStructure(SignBlock.ROTATION) * 360) / 16.0F);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
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
                this.textRenderer.draw(orderedText, t, (float)(s * 10 - 20), q, false, matrixStack.peek().getPositionMatrix(), vertexConsumerProvider, TextRenderer.TextLayerType.NORMAL, 0, r);
            }

        }


        matrixStack.pop();
        renderGrave(blockState, f, matrixStack, vertexConsumerProvider, i, j, world);

    }

    public void renderGrave(BlockState state, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, World world) {
        matrixStack.push();
        matrixStack.translate(0.5, 0.43, 0.5);
        matrixStack.scale(2.28F, 2.15F, 2.28F);
        float rotation = state.get(GravestoneBlock.FACING).asRotation();
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
        //MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.GROUND, i, j, matrixStack, vertexConsumerProvider, 2);
        MinecraftClient.getInstance().getItemRenderer().renderItem(new ItemStack(state.getBlock().asItem(), 1), ModelTransformationMode.GROUND, i, j, matrixStack, vertexConsumerProvider, world, 2);
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

    static int getColor(GravestoneBlockEntity sign) {
        int i = sign.getTextColor().getSignColor();
        if (i == DyeColor.BLACK.getSignColor() && sign.isGlowingText()) {
            return -988212;
        } else {
            double d = 0.4D;
            int j = (int)((double) ColorHelper.Argb.getRed(i) * 0.4D);
            int k = (int)((double) ColorHelper.Argb.getGreen(i) * 0.4D);
            int l = (int)((double) ColorHelper.Argb.getBlue(i) * 0.4D);
            return ColorHelper.Argb.getArgb(0, j, k, l);
        }
    }

    public static SignBlockEntityRenderer.SignModel createSignModel(EntityModelLoader entityModelLoader, WoodType type) {
        return new SignBlockEntityRenderer.SignModel(entityModelLoader.getModelPart(EntityModelLayers.createSign(type)));
    }


    static {
        defaultLayer = RenderLayer.getEntitySolid(new Identifier("textures/entity/signs/oak.png"));
        RenderLayer layer1 = RenderLayer.getEntitySolid(TGBlocks.POLISHED_BASALT_GRAVESTONE_TEXTURE);
        RenderLayer layer2 = RenderLayer.getEntitySolid(TGBlocks.COBBLESTONE_GRAVESTONE_TEXTURE);
        RenderLayer layer3 = RenderLayer.getEntitySolid(TGBlocks.MOSSY_COBBLESTONE_GRAVESTONE_TEXTURE);
        RenderLayer layer4 = RenderLayer.getEntitySolid(TGBlocks.DEEPSLATE_GRAVESTONE_TEXTURE);
        RenderLayer layer5 = RenderLayer.getEntitySolid(TGBlocks.BLACKSTONE_GRAVESTONE_TEXTURE);
        RenderLayer layer6 = RenderLayer.getEntitySolid(TGBlocks.CRACKED_BLACKSTONE_GRAVESTONE_TEXTURE);
        RenderLayer layer7 = RenderLayer.getEntitySolid(TGBlocks.STONE_BRICKS_GRAVESTONE_TEXTURE);
        RenderLayer layer8 = RenderLayer.getEntitySolid(TGBlocks.MOSSY_STONE_BRICKS_GRAVESTONE_TEXTURE);
        RenderLayer layer9 = RenderLayer.getEntitySolid(TGBlocks.BRICKS_GRAVESTONE_TEXTURE);
        RenderLayer layer10 = RenderLayer.getEntitySolid(TGBlocks.RED_SANDSTONE_GRAVESTONE_TEXTURE);
        RenderLayer layer11 = RenderLayer.getEntitySolid(TGBlocks.SANDSTONE_GRAVESTONE_TEXTURE);
        RenderLayer layer12 = RenderLayer.getEntitySolid(TGBlocks.GILDED_BLACKSTONE_GRAVESTONE_TEXTURE);
        RenderLayer layer13 = RenderLayer.getEntitySolid(TGBlocks.QUARTZ_BRICKS_GRAVESTONE_TEXTURE);
        LAYERS.put(TGBlocks.GRAVESTONE, layer1);
        LAYERS.put(TGBlocks.COBBLESTONE_GRAVESTONE, layer2);
        LAYERS.put(TGBlocks.MOSSY_COBBLESTONE_GRAVESTONE, layer3);
        LAYERS.put(TGBlocks.DEEPSLATE_GRAVESTONE, layer4);
        LAYERS.put(TGBlocks.BLACKSTONE_GRAVESTONE, layer5);
        LAYERS.put(TGBlocks.CRACKED_BLACKSTONE_GRAVESTONE, layer6);
        LAYERS.put(TGBlocks.STONE_BRICKS_GRAVESTONE, layer7);
        LAYERS.put(TGBlocks.MOSSY_STONE_BRICKS_GRAVESTONE, layer8);
        LAYERS.put(TGBlocks.BRICKS_GRAVESTONE, layer9);
        LAYERS.put(TGBlocks.RED_SANDSTONE_GRAVESTONE, layer10);
        LAYERS.put(TGBlocks.SANDSTONE_GRAVESTONE, layer11);
        LAYERS.put(TGBlocks.GILDED_BLACKSTONE_GRAVESTONE, layer12);
        LAYERS.put(TGBlocks.QUARTZ_BRICKS_GRAVESTONE, layer13);

    }



    public static VertexConsumer getConsumer(VertexConsumerProvider provider, Block block) {
        return provider.getBuffer(LAYERS.getOrDefault(block, defaultLayer));
    }
}

