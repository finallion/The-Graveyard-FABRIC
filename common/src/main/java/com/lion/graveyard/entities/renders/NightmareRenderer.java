package com.lion.graveyard.entities.renders;

import com.lion.graveyard.entities.NightmareEntity;
import com.lion.graveyard.entities.models.NightmareModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class NightmareRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<NightmareEntity, R> {

    public NightmareRenderer(EntityRendererProvider.Context context) {
        super(context, new NightmareModel());
        this.shadowRadius = 0.4F;
    }

    // stops the vanilla death animation
    @Override
    protected float getDeathMaxRotation(GeoRenderState renderState) {
        return 0.0F;
    }

}
