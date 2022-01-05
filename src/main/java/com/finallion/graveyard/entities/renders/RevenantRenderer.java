package com.finallion.graveyard.entities.renders;

import com.finallion.graveyard.entities.ReaperEntity;
import com.finallion.graveyard.entities.RevenantEntity;
import com.finallion.graveyard.entities.models.ReaperModel;
import com.finallion.graveyard.entities.models.RevenantModel;
import com.finallion.graveyard.entities.renders.features.ReaperEyesFeatureRenderer;
import com.finallion.graveyard.entities.renders.features.RevenantEyesFeatureRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class RevenantRenderer extends GeoEntityRenderer<RevenantEntity> {

    public RevenantRenderer(EntityRendererFactory.Context context) {
        super(context, new RevenantModel());
        this.addLayer(new RevenantEyesFeatureRenderer(this));
        this.shadowRadius = 0.4F;
    }

    // stops the vanilla death animation
    @Override
    protected float getDeathMaxRotation(RevenantEntity entityLivingBaseIn) {
        return 0.0F;
    }




}
