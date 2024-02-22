package com.lion.graveyard.entities.renders.features;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.WraithEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class WraithEyesFeatureRenderer extends GeoRenderLayer<WraithEntity> {
    private final RenderType TEXTURE = RenderType.eyes(new ResourceLocation(Graveyard.MOD_ID,"textures/entity/wraith_eyes.png"));
    private final GeoRenderer<WraithEntity> renderer;

    public WraithEyesFeatureRenderer(GeoRenderer<WraithEntity> entityRendererIn) {
        super(entityRendererIn);
        this.renderer = entityRendererIn;
    }

    @Override
    public void render(PoseStack poseStack, WraithEntity animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        VertexConsumer vertexConsumer = bufferSource.getBuffer(TEXTURE);
        renderer.actuallyRender(
                poseStack,
                animatable,
                bakedModel,
                renderType,
                bufferSource,
                vertexConsumer,
                true,
                partialTick,
                15728640,
                OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 1.0F
        );
    }


}

