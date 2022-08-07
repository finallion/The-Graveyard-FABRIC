package com.finallion.graveyard.entities.renders;

import com.finallion.graveyard.entities.FallingCorpse;
import com.finallion.graveyard.entities.NightmareEntity;
import com.finallion.graveyard.entities.models.FallingCorpseModel;
import com.finallion.graveyard.entities.models.NightmareModel;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class FallingCorpseRenderer extends GeoEntityRenderer<FallingCorpse> {

    public FallingCorpseRenderer(EntityRendererFactory.Context context) {
        super(context, new FallingCorpseModel());
        this.shadowRadius = 0.4F;
    }

    // stops the vanilla death animation
    @Override
    protected float getDeathMaxRotation(FallingCorpse entityLivingBaseIn) {
        return 0.0F;
    }

    @Override
    public Color getRenderColor(FallingCorpse animatable, float partialTicks, MatrixStack stack, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn) {
        return super.getRenderColor(animatable, partialTicks, stack, renderTypeBuffer, vertexBuilder, packedLightIn);
    }
}
