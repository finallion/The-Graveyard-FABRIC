package com.lion.graveyard.entities.renders;

import com.lion.graveyard.entities.CorvidEntity;
import com.lion.graveyard.entities.models.CorvidModel;
import com.lion.graveyard.entities.renders.features.CorvidHeldItemLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class CorvidRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<CorvidEntity, R> {

    public CorvidRenderer(EntityRendererProvider.Context context) {
        super(context, new CorvidModel());
        this.shadowRadius = 0.2F;
        this.addRenderLayer(new CorvidHeldItemLayer<>(this));
    }

    @Override
    protected float getDeathMaxRotation(GeoRenderState renderState) {
        // The death animation lays the bird down.
        return 0.0F;
    }

}
