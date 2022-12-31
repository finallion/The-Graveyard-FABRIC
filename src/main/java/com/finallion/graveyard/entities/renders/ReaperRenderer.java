package com.finallion.graveyard.entities.renders;

import com.finallion.graveyard.entities.ReaperEntity;
import com.finallion.graveyard.entities.models.ReaperModel;
import com.finallion.graveyard.entities.renders.features.ReaperEyesFeatureRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ReaperRenderer extends GeoEntityRenderer<ReaperEntity> {

    public ReaperRenderer(EntityRendererFactory.Context context) {
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
