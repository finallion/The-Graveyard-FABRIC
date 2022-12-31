package com.finallion.graveyard.entities.renders.features;


import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.entities.GhoulEntity;
import com.finallion.graveyard.entities.RevenantEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;


public class GhoulEyesFeatureRenderer extends GeoRenderLayer<GhoulEntity> {
    private RenderLayer TEXTURE;
    private final GeoRenderer<GhoulEntity> renderer;

    public GhoulEyesFeatureRenderer(GeoRenderer<GhoulEntity> entityRendererIn) {
        super(entityRendererIn);
        this.renderer = entityRendererIn;
    }

    @Override
    public void render(MatrixStack poseStack, GhoulEntity animatable, BakedGeoModel bakedModel, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        if (animatable.getVariant() == 10) {
            TEXTURE = RenderLayer.getEyes(new Identifier(TheGraveyard.MOD_ID, "textures/entity/ghoul_eyes_skin9.png"));
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
                    OverlayTexture.DEFAULT_UV,
                    1.0F, 1.0F, 1.0F, 1.0F
            );
        }
    }


}

