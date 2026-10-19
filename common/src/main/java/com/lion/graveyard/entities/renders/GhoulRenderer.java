package com.lion.graveyard.entities.renders;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.client.GraveyardDataTickets;
import com.lion.graveyard.entities.GhoulEntity;
import com.lion.graveyard.entities.models.BaseGhoulModel;
import com.lion.graveyard.entities.renders.features.GraveyardEyesLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class GhoulRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<GhoulEntity, R> {

    public GhoulRenderer(EntityRendererProvider.Context context) {
        super(context, new BaseGhoulModel());
        this.shadowRadius = 0.7F;
        this.addRenderLayer(new GraveyardEyesLayer<>(this, GhoulRenderer::eyesTexture));
    }

    // stops the vanilla death animation
    @Override
    protected float getDeathMaxRotation(GeoRenderState renderState) {
        return 0.0F;
    }

    private static ResourceLocation eyesTexture(GeoRenderState renderState) {
        return Graveyard.id("textures/entity/ghoul_eyes_skin" + renderState.getGeckolibData(GraveyardDataTickets.GHOUL_VARIANT) + ".png");
    }

}
