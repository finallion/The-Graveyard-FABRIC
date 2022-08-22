package com.finallion.graveyard.entities.renders.features;


import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.entities.LichEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;


public class LichEyesFeatureRenderer extends GeoLayerRenderer<LichEntity> {
    private RenderLayer TEXTURE;
    private final IGeoRenderer<LichEntity> renderer;

    public LichEyesFeatureRenderer(IGeoRenderer<LichEntity> entityRendererIn) {
        super(entityRendererIn);
        this.renderer = entityRendererIn;
    }

    @Override
    public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, LichEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

        TEXTURE = RenderLayer.getEyes(new Identifier(TheGraveyard.MOD_ID, "textures/entity/lich_eye_texture.png"));
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

