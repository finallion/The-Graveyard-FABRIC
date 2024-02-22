package com.lion.graveyard.entities.renders;

import com.lion.graveyard.entities.RevenantEntity;
import com.lion.graveyard.entities.models.RevenantModel;
import com.lion.graveyard.entities.renders.features.RevenantEyesFeatureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;


public class RevenantRenderer extends GeoEntityRenderer<RevenantEntity> {

    public RevenantRenderer(EntityRendererProvider.Context context) {
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
    public RenderType getRenderType(RevenantEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutoutNoCull(texture);
    }


}
