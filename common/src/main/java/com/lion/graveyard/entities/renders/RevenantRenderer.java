package com.lion.graveyard.entities.renders;

import com.lion.graveyard.entities.RevenantEntity;
import com.lion.graveyard.entities.models.RevenantModel;
import com.lion.graveyard.entities.renders.features.RevenantEyesFeatureRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;


public class RevenantRenderer extends GeoEntityRenderer<RevenantEntity> {

    public RevenantRenderer(EntityRendererFactory.Context context) {
        super(context, new RevenantModel());
        this.addRenderLayer(new RevenantEyesFeatureRenderer(this));
        this.shadowRadius = 0.4F;
    }

    // stops the vanilla death animation
    @Override
    protected float getDeathMaxRotation(RevenantEntity entityLivingBaseIn) {
        return 0.0F;
    }

    @Override
    public RenderLayer getRenderType(RevenantEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return RenderLayer.getEntityCutoutNoCull(texture);
    }


}
