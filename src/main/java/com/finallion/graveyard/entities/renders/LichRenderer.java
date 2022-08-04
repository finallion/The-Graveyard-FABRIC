package com.finallion.graveyard.entities.renders;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.entities.GhoulEntity;
import com.finallion.graveyard.entities.LichEntity;
import com.finallion.graveyard.entities.models.BaseGhoulModel;
import com.finallion.graveyard.entities.models.LichModel;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class LichRenderer extends GeoEntityRenderer<LichEntity> {

    public LichRenderer(EntityRendererFactory.Context context) {
        super(context, new LichModel());
        this.shadowRadius = 1.0F;
    }

    @Override
    public RenderLayer getRenderType(LichEntity animatable, float partialTicks, MatrixStack stack, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, Identifier textureLocation) {
        return RenderLayer.getEntityCutoutNoCull(textureLocation);
    }

    // stops the vanilla death animation
    @Override
    protected float getDeathMaxRotation(LichEntity entityLivingBaseIn) {
        return 0.0F;
    }

}
