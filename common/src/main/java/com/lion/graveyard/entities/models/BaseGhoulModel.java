package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.client.GraveyardDataTickets;
import com.lion.graveyard.entities.GhoulEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class BaseGhoulModel extends GeoModel<GhoulEntity> {
    @Override
    public ResourceLocation getModelResource(GeoRenderState renderState) {
        return Graveyard.id("ghoul");
    }

    @Override
    public ResourceLocation getTextureResource(GeoRenderState renderState) {
        return Graveyard.id("textures/entity/ghoul_skin" + renderState.getGeckolibData(GraveyardDataTickets.GHOUL_VARIANT) + ".png");
    }

    @Override
    public void addAdditionalStateData(GhoulEntity animatable, GeoRenderState renderState) {
        renderState.addGeckolibData(GraveyardDataTickets.GHOUL_VARIANT, animatable.getVariant());
    }

    @Override
    public ResourceLocation getAnimationResource(GhoulEntity animatable) {
        return Graveyard.id("ghoul/ghoul");
    }
}
