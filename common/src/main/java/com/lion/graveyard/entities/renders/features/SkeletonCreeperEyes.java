package com.lion.graveyard.entities.renders.features;

import com.lion.graveyard.Graveyard;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.monster.creeper.CreeperModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;

@Environment(EnvType.CLIENT)
public class SkeletonCreeperEyes extends RenderLayer<CreeperRenderState, CreeperModel> {

    public SkeletonCreeperEyes(RenderLayerParent<CreeperRenderState, CreeperModel> context) {
        super(context);
    }

    @Override
    public void render(
            PoseStack matrices,
            MultiBufferSource vertexConsumers,
            int light,
            CreeperRenderState renderState,
            float yRot,
            float xRot
    ) {
        VertexConsumer vertex = vertexConsumers.getBuffer(RenderTypes.eyes(Graveyard.id("textures/entity/skeleton_creeper_eyes.png")));

        this.getParentModel().renderToBuffer(matrices, vertex, 15728640, OverlayTexture.NO_OVERLAY, -1);
    }
}

