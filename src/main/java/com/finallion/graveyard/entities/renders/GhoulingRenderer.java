package com.finallion.graveyard.entities.renders;

import com.finallion.graveyard.entities.GhoulingEntity;
import com.finallion.graveyard.entities.LichEntity;
import com.finallion.graveyard.entities.models.GhoulingModel;
import com.finallion.graveyard.entities.models.LichModel;
import com.finallion.graveyard.entities.renders.features.LichEyesFeatureRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class GhoulingRenderer extends GeoEntityRenderer<GhoulingEntity> {

    public GhoulingRenderer(EntityRendererFactory.Context context) {
        super(context, new GhoulingModel());
        this.shadowRadius = 0.6F;
    }

    @Override
    public RenderLayer getRenderType(GhoulingEntity animatable, float partialTicks, MatrixStack stack, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, Identifier textureLocation) {
        return RenderLayer.getEntityCutoutNoCull(textureLocation);
    }

    // stops the vanilla death animation
    @Override
    protected float getDeathMaxRotation(GhoulingEntity entityLivingBaseIn) {
        return 0.0F;
    }

}
