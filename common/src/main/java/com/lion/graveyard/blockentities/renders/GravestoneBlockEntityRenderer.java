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
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;
import java.util.HashMap;
import java.util.List;

@Environment(EnvType.CLIENT)
public class GravestoneBlockEntityRenderer
        implements BlockEntityRenderer<GravestoneBlockEntity, GravestoneBlockEntityRenderer.State> {
    private static final int RENDER_DISTANCE = Mth.square(16);
    private final Font textRenderer;
    private final ItemModelResolver itemModelResolver;
    private static final HashMap<Block, RenderType> LAYERS = Maps.newHashMap();
    private static RenderType defaultLayer;

    public GravestoneBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.textRenderer = ctx.font();
        this.itemModelResolver = ctx.itemModelResolver();
    }

    public static class State extends BlockEntityRenderState {
        public FormattedCharSequence[] lines = new FormattedCharSequence[0];
        public int textColor;
        public int outlineColor;
        public int textLight;
        public final ItemStackRenderState stone = new ItemStackRenderState();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(
            GravestoneBlockEntity gravestone,
            State state,
            float partialTick,
            Vec3 cameraPosition,
            ModelFeatureRenderer.CrumblingOverlay crumblingOverlay
    ) {
        BlockEntityRenderer.super.extractRenderState(gravestone, state, partialTick, cameraPosition, crumblingOverlay);
        int darkColor = getColor(gravestone);

        state.lines = gravestone.getText().getRenderMessages(Minecraft.getInstance().isTextFilteringEnabled(), (text) -> {
            List<FormattedCharSequence> list = this.textRenderer.split(text, gravestone.getMaxTextWidth());
            return list.isEmpty() ? FormattedCharSequence.EMPTY : list.get(0);
        });

        if (gravestone.getText().hasGlowingText()) {
            state.textColor = gravestone.getText().getColor().getTextColor();
            state.outlineColor = shouldRender(gravestone, state.textColor) ? darkColor : 0;
            state.textLight = 15728880;
        } else {
            state.textColor = darkColor;
            state.outlineColor = 0;
            state.textLight = state.lightCoords;
        }

        state.stone.clear();
        ItemStack stack = new ItemStack(state.blockState.getBlock().asItem(), 1);
        this.itemModelResolver.updateForTopItem(state.stone, stack, ItemDisplayContext.GROUND, gravestone.getLevel(), null, 2);
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        poseStack.pushPose();

        poseStack.translate(0.5D, 0.25D, 0.5D);

        float rotation = -state.blockState.getValue(GravestoneBlock.FACING).toYRot();
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        poseStack.translate(0.0D, 0.3333333432674408D, 0.23D);

        poseStack.scale(0.010416667F, -0.010416667F, 0.010416667F);

        for (int line = 0; line < 4; ++line) {
            FormattedCharSequence text = state.lines[line];
            float x = (float) (-this.textRenderer.width(text) / 2);

            collector.submitText(
                    poseStack,
                    x,
                    (float) (line * 10 - 20),
                    text,
                    false,
                    Font.DisplayMode.NORMAL,
                    state.textLight,
                    state.textColor,
                    0,
                    state.outlineColor
            );
        }

        poseStack.popPose();

        submitGrave(state, poseStack, collector);
    }

    private void submitGrave(State state, PoseStack poseStack, SubmitNodeCollector collector) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.43, 0.5);
        poseStack.scale(2.28F, 2.15F, 2.28F);
        float rotation = state.blockState.getValue(GravestoneBlock.FACING).toYRot();
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));

        state.stone.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
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
            int j = (int)((double) ARGB.red(i) * d);
            int k = (int)((double) ARGB.green(i) * d);
            int l = (int)((double) ARGB.blue(i) * d);
            return ARGB.color(255, j, k, l);
        }
    }

    public static Model.Simple createSignModel(EntityModelSet entityModelLoader, WoodType type) {
        return SignRenderer.createSignModel(entityModelLoader, type, false);
    }

    static {
        defaultLayer = RenderTypes.entitySolid(Identifier.withDefaultNamespace("textures/entity/signs/oak.png"));
        LAYERS.put(TGBlocks.GRAVESTONE.get(), RenderTypes.entitySolid(GravestoneIdentifier.POLISHED_BASALT_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.COBBLESTONE_GRAVESTONE.get(), RenderTypes.entitySolid(GravestoneIdentifier.COBBLESTONE_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.MOSSY_COBBLESTONE_GRAVESTONE.get(), RenderTypes.entitySolid(GravestoneIdentifier.MOSSY_COBBLESTONE_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.DEEPSLATE_GRAVESTONE.get(), RenderTypes.entitySolid(GravestoneIdentifier.DEEPSLATE_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.BLACKSTONE_GRAVESTONE.get(), RenderTypes.entitySolid(GravestoneIdentifier.BLACKSTONE_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.CRACKED_BLACKSTONE_GRAVESTONE.get(), RenderTypes.entitySolid(GravestoneIdentifier.CRACKED_BLACKSTONE_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.STONE_BRICKS_GRAVESTONE.get(), RenderTypes.entitySolid(GravestoneIdentifier.STONE_BRICKS_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.MOSSY_STONE_BRICKS_GRAVESTONE.get(), RenderTypes.entitySolid(GravestoneIdentifier.MOSSY_STONE_BRICKS_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.BRICKS_GRAVESTONE.get(), RenderTypes.entitySolid(GravestoneIdentifier.BRICKS_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.RED_SANDSTONE_GRAVESTONE.get(), RenderTypes.entitySolid(GravestoneIdentifier.RED_SANDSTONE_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.SANDSTONE_GRAVESTONE.get(), RenderTypes.entitySolid(GravestoneIdentifier.SANDSTONE_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.GILDED_BLACKSTONE_GRAVESTONE.get(), RenderTypes.entitySolid(GravestoneIdentifier.GILDED_BLACKSTONE_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.QUARTZ_BRICKS_GRAVESTONE.get(), RenderTypes.entitySolid(GravestoneIdentifier.QUARTZ_BRICKS_GRAVESTONE_TEXTURE));
    }

    public static VertexConsumer getConsumer(MultiBufferSource provider, Block block) {
        return provider.getBuffer(LAYERS.getOrDefault(block, defaultLayer));
    }
}

