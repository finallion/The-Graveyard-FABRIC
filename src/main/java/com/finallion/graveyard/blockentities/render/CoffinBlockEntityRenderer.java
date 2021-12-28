package com.finallion.graveyard.blockentities.render;

import com.finallion.graveyard.blockentities.CoffinBlockEntity;
import com.finallion.graveyard.blockentities.model.CoffinModel;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class CoffinBlockEntityRenderer extends GeoBlockRenderer<CoffinBlockEntity> {


    public CoffinBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(new CoffinModel());
    }

    @Override
    public RenderLayer getRenderType(CoffinBlockEntity animatable, float partialTicks, MatrixStack stack, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, Identifier textureLocation) {
        return RenderLayer.getEntityTranslucent(getTextureLocation(animatable));
    }
}
