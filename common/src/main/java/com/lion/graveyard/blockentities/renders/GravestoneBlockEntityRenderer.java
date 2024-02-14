package com.lion.graveyard.blockentities.renders;

import com.google.common.collect.Maps;
import com.lion.graveyard.blockentities.GravestoneBlockEntity;
import com.lion.graveyard.blocks.GravestoneBlock;
import com.lion.graveyard.init.TGBlocks;
import com.lion.graveyard.util.GravestoneIdentifier;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.List;

@Environment(EnvType.CLIENT)
public class GravestoneBlockEntityRenderer implements BlockEntityRenderer<GravestoneBlockEntity> {
    private static final int RENDER_DISTANCE = Mth.square(16);
    private final Font textRenderer;
    private static final HashMap<Block, RenderType> LAYERS = Maps.newHashMap();
    private static RenderType defaultLayer;

    public GravestoneBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.textRenderer = ctx.getFont();
    }

    public void render(GravestoneBlockEntity signBlockEntity, float f, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, int j) {
        BlockState blockState = signBlockEntity.getBlockState();
        Level world = signBlockEntity.getLevel();
        matrixStack.pushPose();

        // text render location in world
        // offset on block
        matrixStack.translate(0.5D, 0.25D, 0.5D);

        float rotation = -(blockState.getValue(GravestoneBlock.FACING).toYRot());
        matrixStack.mulPose(Axis.YP.rotationDegrees(rotation));
        matrixStack.pushPose();

        // size of block
        matrixStack.scale(0.6666667F, -0.6666667F, -0.6666667F);
        matrixStack.popPose();
        matrixStack.translate(0.0D, 0.3333333432674408D, 0.23);
        matrixStack.scale(0.010416667F, -0.010416667F, 0.010416667F);

        int m = getColor(signBlockEntity);

        FormattedCharSequence[] orderedTexts = signBlockEntity.getText().getRenderMessages(Minecraft.getInstance().isTextFilteringEnabled(), (text) -> {
            List<FormattedCharSequence> list = this.textRenderer.split(text, signBlockEntity.getMaxTextWidth());
            return list.isEmpty() ? FormattedCharSequence.EMPTY : (FormattedCharSequence)list.get(0);
        });

        int q;
        boolean bl2;
        int r;
        if (signBlockEntity.getText().hasGlowingText()) {
            q = signBlockEntity.getText().getColor().getTextColor();
            bl2 = shouldRender(signBlockEntity, q);
            r = 15728880;
        } else {
            q = m;
            bl2 = false;
            r = i;
        }

        for(int s = 0; s < 4; ++s) {
            FormattedCharSequence orderedText = orderedTexts[s];
            float t = (float)(-this.textRenderer.width(orderedText) / 2);
            if (bl2) {
                this.textRenderer.drawInBatch8xOutline(orderedText, t, (float)(s * 10 - 20), q, m, matrixStack.last().pose(), vertexConsumerProvider, r);
            } else {
                this.textRenderer.drawInBatch(orderedText, t, (float)(s * 10 - 20), q, false, matrixStack.last().pose(), vertexConsumerProvider, Font.DisplayMode.POLYGON_OFFSET, 0, r);
            }

        }

        matrixStack.popPose();
        renderGrave(blockState, f, matrixStack, vertexConsumerProvider, i, j, world);
    }

    public void renderGrave(BlockState state, float f, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, int j, Level world) {
        matrixStack.pushPose();
        matrixStack.translate(0.5, 0.43, 0.5);
        matrixStack.scale(2.28F, 2.15F, 2.28F);
        float rotation = state.getValue(GravestoneBlock.FACING).asYRot();
        matrixStack.mulPose(Axis.YP.rotationDegrees(rotation));
        Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(state.getBlock().asItem(), 1), ItemDisplayContext.GROUND, i, j, matrixStack, vertexConsumerProvider, world, 2);
        matrixStack.popPose();
    }

    private static boolean shouldRender(GravestoneBlockEntity sign, int signColor) {
        if (signColor == DyeColor.BLACK.getTextColor()) {
            return true;
        } else {
            Minecraft minecraftClient = Minecraft.getInstance();
            LocalPlayer clientPlayerEntity = minecraftClient.player;
            if (clientPlayerEntity != null && minecraftClient.options.getCameraType().isFirstPerson() && clientPlayerEntity.isScoping()) {
                return true;
            } else {
                Entity entity = minecraftClient.getCameraEntity();
                return entity != null && entity.distanceToSqr(Vec3.atCenterOf(sign.getBlockPos())) < (double)RENDER_DISTANCE;
            }
        }
    }

    static int getColor(GravestoneBlockEntity sign) {
        int i = sign.getText().getColor().getTextColor();
        if (i == DyeColor.BLACK.getTextColor() && sign.getText().hasGlowingText()) {
            return -988212;
        } else {
            double d = 0.4D;
            int j = (int)((double) FastColor.ARGB32.red(i) * 0.4);
            int k = (int)((double) FastColor.ARGB32.green(i) * 0.4);
            int l = (int)((double) FastColor.ARGB32.blue(i) * 0.4);
            return FastColor.ARGB32.color(0, j, k, l);
        }
    }

    public static SignRenderer.SignModel createSignModel(EntityModelSet entityModelLoader, WoodType type) {
        return new SignRenderer.SignModel(entityModelLoader.bakeLayer(ModelLayers.createSignModelName(type)));
    }

    static {
        defaultLayer = RenderType.entitySolid(new ResourceLocation("textures/entity/signs/oak.png"));
        LAYERS.put(TGBlocks.GRAVESTONE.get(), RenderType.entitySolid(GravestoneIdentifier.POLISHED_BASALT_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.COBBLESTONE_GRAVESTONE.get(), RenderType.entitySolid(GravestoneIdentifier.COBBLESTONE_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.MOSSY_COBBLESTONE_GRAVESTONE.get(), RenderType.entitySolid(GravestoneIdentifier.MOSSY_COBBLESTONE_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.DEEPSLATE_GRAVESTONE.get(), RenderType.entitySolid(GravestoneIdentifier.DEEPSLATE_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.BLACKSTONE_GRAVESTONE.get(), RenderType.entitySolid(GravestoneIdentifier.BLACKSTONE_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.CRACKED_BLACKSTONE_GRAVESTONE.get(), RenderType.entitySolid(GravestoneIdentifier.CRACKED_BLACKSTONE_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.STONE_BRICKS_GRAVESTONE.get(), RenderType.entitySolid(GravestoneIdentifier.STONE_BRICKS_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.MOSSY_STONE_BRICKS_GRAVESTONE.get(), RenderType.entitySolid(GravestoneIdentifier.MOSSY_STONE_BRICKS_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.BRICKS_GRAVESTONE.get(), RenderType.entitySolid(GravestoneIdentifier.BRICKS_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.RED_SANDSTONE_GRAVESTONE.get(), RenderType.entitySolid(GravestoneIdentifier.RED_SANDSTONE_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.SANDSTONE_GRAVESTONE.get(), RenderType.entitySolid(GravestoneIdentifier.SANDSTONE_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.GILDED_BLACKSTONE_GRAVESTONE.get(), RenderType.entitySolid(GravestoneIdentifier.GILDED_BLACKSTONE_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.QUARTZ_BRICKS_GRAVESTONE.get(), RenderType.entitySolid(GravestoneIdentifier.QUARTZ_BRICKS_GRAVESTONE_TEXTURE));
    }

    public static VertexConsumer getConsumer(MultiBufferSource provider, Block block) {
        return provider.getBuffer(LAYERS.getOrDefault(block, defaultLayer));
    }
}

