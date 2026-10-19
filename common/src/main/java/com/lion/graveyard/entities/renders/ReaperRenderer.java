package com.lion.graveyard.entities.renders;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.ReaperEntity;
import com.lion.graveyard.entities.models.ReaperModel;
import com.lion.graveyard.entities.renders.features.GraveyardEyesLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class ReaperRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<ReaperEntity, R> {
    private static final ResourceLocation EYES = Graveyard.id("textures/entity/reaper_eyes.png");

    public ReaperRenderer(EntityRendererProvider.Context context) {
        super(context, new ReaperModel());
        this.addRenderLayer(new GraveyardEyesLayer<>(this, EYES));
        this.shadowRadius = 0.4F;
    }

    // stops the vanilla death animation
    @Override
    protected float getDeathMaxRotation(GeoRenderState renderState) {
        return 0.0F;
    }

}
