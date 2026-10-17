package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.GhoulEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class BaseGhoulModel extends GeoModel<GhoulEntity> {

    @Override
    public Identifier getModelResource(GhoulEntity object) {
        return Graveyard.id("geo/ghoul.geo.json");
    }

    @Override
    public Identifier getTextureResource(GhoulEntity object) {
        return Graveyard.id("textures/entity/ghoul_skin" + object.getVariant() + ".png");
    }

    @Override
    public Identifier getAnimationResource(GhoulEntity animatable) {
        return Graveyard.id("animations/ghoul/ghoul.animation.json");
    }
}
