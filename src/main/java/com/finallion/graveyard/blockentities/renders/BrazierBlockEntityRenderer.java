package com.finallion.graveyard.blockentities.renders;

import com.finallion.graveyard.blockentities.BrazierBlockEntity;
import com.finallion.graveyard.blockentities.models.BrazierModel;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class BrazierBlockEntityRenderer extends GeoBlockRenderer<BrazierBlockEntity> {

    public BrazierBlockEntityRenderer() {
        super(new BrazierModel());
    }

    @Override
    public RenderLayer getRenderType(BrazierBlockEntity animatable, float partialTicks, MatrixStack stack, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, Identifier textureLocation) {
        return RenderLayer.getEntityTranslucent(getTextureLocation(animatable));
    }
}
