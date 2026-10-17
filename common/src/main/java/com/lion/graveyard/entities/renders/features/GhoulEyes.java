package com.lion.graveyard.entities.renders.features;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.GhoulEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
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
    public void render(MatrixStack matrices, GhoulEntity entity, BakedGeoModel model, RenderLayer renderType, VertexConsumerProvider vertexConsumers, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        RenderLayer eyes = RenderLayer.getEyes(Graveyard.id("textures/entity/ghoul_eyes_skin" + entity.getVariant() + ".png"));
        this.renderer.actuallyRender(matrices, entity, model, renderType, vertexConsumers, vertexConsumers.getBuffer(eyes), true, partialTick, FULL_LIGHT, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
