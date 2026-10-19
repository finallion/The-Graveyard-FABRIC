package com.lion.graveyard.gui;

import com.lion.graveyard.blockentities.renders.GravestoneBlockEntityRenderer;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;

/// The vanilla sign renderer supports wood types only.
@Environment(EnvType.CLIENT)
public class GravestoneSignRenderer extends PictureInPictureRenderer<GravestoneSignRenderState> {

    public GravestoneSignRenderer(MultiBufferSource.BufferSource bufferSource) {
        super(bufferSource);
    }

    @Override
    public Class<GravestoneSignRenderState> getRenderStateClass() {
        return GravestoneSignRenderState.class;
    }

    @Override
    protected void renderToTexture(GravestoneSignRenderState state, PoseStack poseStack) {
        Minecraft.getInstance()
            .gameRenderer
            .getLighting()
            .setupFor(Lighting.Entry.ITEMS_FLAT);
        poseStack.translate(0.0F, -0.75F, 0.0F);
        VertexConsumer consumer = GravestoneBlockEntityRenderer.getConsumer(this.bufferSource, state.block());
        state.model().renderToBuffer(poseStack, consumer, 15728880, OverlayTexture.NO_OVERLAY);
    }

    @Override
    protected String getTextureLabel() {
        return "graveyard gravestone sign";
    }
}
