package com.lion.graveyard.entities.renders;

import com.lion.graveyard.entities.ReaperEntity;
import com.lion.graveyard.entities.models.ReaperModel;
import com.lion.graveyard.entities.renders.features.ReaperEyesFeatureRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ReaperRenderer extends GeoEntityRenderer<ReaperEntity> {

    public ReaperRenderer(EntityRendererProvider.Context context) {
        super(context, new ReaperModel());
        this.addRenderLayer(new ReaperEyesFeatureRenderer(this));
        this.shadowRadius = 0.4F;
    }

    // stops the vanilla death animation
    @Override
    protected float getDeathMaxRotation(ReaperEntity entityLivingBaseIn) {
        return 0.0F;
    }




}
