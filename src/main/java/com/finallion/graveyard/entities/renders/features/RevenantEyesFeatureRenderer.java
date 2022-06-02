package com.finallion.graveyard.entities.renders.features;


import com.finallion.graveyard.entities.ReaperEntity;
import com.finallion.graveyard.entities.RevenantEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;


public class RevenantEyesFeatureRenderer extends GeoLayerRenderer<RevenantEntity> {
    private final RenderLayer TEXTURE = RenderLayer.getEyes(new Identifier("graveyard:textures/entity/revenant_eyes.png"));
    private final IGeoRenderer<RevenantEntity> renderer;

    public RevenantEyesFeatureRenderer(IGeoRenderer<RevenantEntity> entityRendererIn) {
        super(entityRendererIn);
        this.renderer = entityRendererIn;
    }

    @Override
    public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, RevenantEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        VertexConsumer vertexConsumer = bufferIn.getBuffer(TEXTURE);
        renderer.render(
                getEntityModel().getModel(getEntityModel().getModelResource(entitylivingbaseIn)),
                entitylivingbaseIn,
                partialTicks,
                TEXTURE,
                matrixStackIn,
                bufferIn,
                vertexConsumer,
                15728640,
                OverlayTexture.DEFAULT_UV,
                1.0F, 1.0F, 1.0F, 1.0F
        );
    }


}

