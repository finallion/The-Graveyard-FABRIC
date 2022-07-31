package com.finallion.graveyard.entities.renders;

import com.finallion.graveyard.entities.GhoulEntity;
import com.finallion.graveyard.entities.LichEntity;
import com.finallion.graveyard.entities.models.BaseGhoulModel;
import com.finallion.graveyard.entities.models.LichModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class LichRenderer extends GeoEntityRenderer<LichEntity> {

    public LichRenderer(EntityRendererFactory.Context context) {
        super(context, new LichModel());
        this.shadowRadius = 1.0F;
    }


    // stops the vanilla death animation
    @Override
    protected float getDeathMaxRotation(LichEntity entityLivingBaseIn) {
        return 0.0F;
    }

}
