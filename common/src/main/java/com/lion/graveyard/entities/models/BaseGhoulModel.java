package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.GhoulEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BaseGhoulModel extends GeoModel<GhoulEntity> {

    @Override
    public ResourceLocation getModelResource(GhoulEntity object) {
        return Graveyard.id("geo/ghoul.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GhoulEntity object) {
        return Graveyard.id("textures/entity/ghoul_skin" + object.getVariant() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(GhoulEntity animatable) {
        return Graveyard.id("animations/ghoul/ghoul.animation.json");
    }
}
