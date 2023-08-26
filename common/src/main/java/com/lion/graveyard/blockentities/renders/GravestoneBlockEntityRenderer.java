package com.lion.graveyard.blockentities.renders;

import com.google.common.collect.Maps;
import com.lion.graveyard.blockentities.GravestoneBlockEntity;
import com.lion.graveyard.blocks.GravestoneBlock;
import com.lion.graveyard.init.TGBlocks;
import com.lion.graveyard.util.GravestoneIdentifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
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
import net.minecraft.client.render.model.json.ModelTransformationMode;
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

@Environment(EnvType.CLIENT)
public class GravestoneBlockEntityRenderer implements BlockEntityRenderer<GravestoneBlockEntity> {
    private static final int RENDER_DISTANCE = MathHelper.square(16);
    private final TextRenderer textRenderer;
    private static final HashMap<Block, RenderLayer> LAYERS = Maps.newHashMap();
    private static RenderLayer defaultLayer;

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
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
        matrixStack.push();

        // size of block
        matrixStack.scale(0.6666667F, -0.6666667F, -0.6666667F);
        matrixStack.pop();
        matrixStack.translate(0.0D, 0.3333333432674408D, 0.23);
        matrixStack.scale(0.010416667F, -0.010416667F, 0.010416667F);

        int m = getColor(signBlockEntity);

        OrderedText[] orderedTexts = signBlockEntity.getText().getOrderedMessages(MinecraftClient.getInstance().shouldFilterText(), (text) -> {
            List<OrderedText> list = this.textRenderer.wrapLines(text, signBlockEntity.getMaxTextWidth());
            return list.isEmpty() ? OrderedText.EMPTY : (OrderedText)list.get(0);
        });

        int q;
        boolean bl2;
        int r;
        if (signBlockEntity.getText().isGlowing()) {
            q = signBlockEntity.getText().getColor().getSignColor();
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
        int i = sign.getText().getColor().getSignColor();
        if (i == DyeColor.BLACK.getSignColor() && sign.getText().isGlowing()) {
            return -988212;
        } else {
            double d = 0.4D;
            int j = (int)((double) ColorHelper.Argb.getRed(i) * d);
            int k = (int)((double) ColorHelper.Argb.getGreen(i) * d);
            int l = (int)((double) ColorHelper.Argb.getBlue(i) * d);
            return ColorHelper.Argb.getArgb(0, j, k, l);
        }
    }

    static {
        defaultLayer = RenderLayer.getEntitySolid(new Identifier("textures/entity/signs/oak.png"));
        LAYERS.put(TGBlocks.GRAVESTONE.get(), RenderLayer.getEntitySolid(GravestoneIdentifier.POLISHED_BASALT_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.COBBLESTONE_GRAVESTONE.get(), RenderLayer.getEntitySolid(GravestoneIdentifier.COBBLESTONE_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.MOSSY_COBBLESTONE_GRAVESTONE.get(), RenderLayer.getEntitySolid(GravestoneIdentifier.MOSSY_COBBLESTONE_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.DEEPSLATE_GRAVESTONE.get(), RenderLayer.getEntitySolid(GravestoneIdentifier.DEEPSLATE_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.BLACKSTONE_GRAVESTONE.get(), RenderLayer.getEntitySolid(GravestoneIdentifier.BLACKSTONE_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.CRACKED_BLACKSTONE_GRAVESTONE.get(), RenderLayer.getEntitySolid(GravestoneIdentifier.CRACKED_BLACKSTONE_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.STONE_BRICKS_GRAVESTONE.get(), RenderLayer.getEntitySolid(GravestoneIdentifier.STONE_BRICKS_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.MOSSY_STONE_BRICKS_GRAVESTONE.get(), RenderLayer.getEntitySolid(GravestoneIdentifier.MOSSY_STONE_BRICKS_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.BRICKS_GRAVESTONE.get(), RenderLayer.getEntitySolid(GravestoneIdentifier.BRICKS_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.RED_SANDSTONE_GRAVESTONE.get(), RenderLayer.getEntitySolid(GravestoneIdentifier.RED_SANDSTONE_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.SANDSTONE_GRAVESTONE.get(), RenderLayer.getEntitySolid(GravestoneIdentifier.SANDSTONE_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.GILDED_BLACKSTONE_GRAVESTONE.get(), RenderLayer.getEntitySolid(GravestoneIdentifier.GILDED_BLACKSTONE_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.QUARTZ_BRICKS_GRAVESTONE.get(), RenderLayer.getEntitySolid(GravestoneIdentifier.QUARTZ_BRICKS_GRAVESTONE_TEXTURE));
    }

    public static VertexConsumer getConsumer(VertexConsumerProvider provider, Block block) {
        return provider.getBuffer(LAYERS.getOrDefault(block, defaultLayer));
    }
}

