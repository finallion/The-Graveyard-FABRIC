package com.finallion.graveyard.entities.renders;

import com.finallion.graveyard.entities.BaseGhoulEntity;
import com.finallion.graveyard.entities.models.BaseGhoulModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class GhoulRenderer extends GeoEntityRenderer<BaseGhoulEntity> {

    public GhoulRenderer(EntityRendererFactory.Context context) {
        super(context, new BaseGhoulModel());
        // glowing eyes
        //this.addLayer(new GhoulEyesFeatureRenderer(this));
        this.shadowRadius = 0.7F;
    }



    // stops the vanilla death animation
    @Override
    protected float getDeathMaxRotation(BaseGhoulEntity entityLivingBaseIn) {
        return 0.0F;
    }

}
