package com.lion.graveyard.entities.renders;

import com.lion.graveyard.entities.GhoulEntity;
import com.lion.graveyard.entities.models.BaseGhoulModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GhoulRenderer extends GeoEntityRenderer<GhoulEntity> {

    public GhoulRenderer(EntityRendererProvider.Context context) {
        super(context, new BaseGhoulModel());
        this.shadowRadius = 0.7F;
    }

    // stops the vanilla death animation
    @Override
    protected float getDeathMaxRotation(GhoulEntity entityLivingBaseIn) {
        return 0.0F;
    }

}
