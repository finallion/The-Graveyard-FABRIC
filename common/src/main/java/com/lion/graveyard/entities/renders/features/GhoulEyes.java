package com.lion.graveyard.entities.renders.features;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.GhoulEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class GhoulEyes extends GeoRenderLayer<GhoulEntity> {
    private static final int FULL_LIGHT = 15728640;

    private final GeoRenderer<GhoulEntity> renderer;

    public GhoulEyes(GeoRenderer<GhoulEntity> renderer) {
        super(renderer);
        this.renderer = renderer;
    }

    @Override
    public void render(PoseStack matrices, GhoulEntity entity, BakedGeoModel model, RenderType renderType, MultiBufferSource vertexConsumers, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        RenderType eyes = RenderType.eyes(Graveyard.id("textures/entity/ghoul_eyes_skin" + entity.getVariant() + ".png"));
        this.renderer.actuallyRender(matrices, entity, model, renderType, vertexConsumers, vertexConsumers.getBuffer(eyes), true, partialTick, FULL_LIGHT, OverlayTexture.NO_OVERLAY, -1);
    }
}
