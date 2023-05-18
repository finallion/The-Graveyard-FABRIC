package com.finallion.graveyard.entities.models;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.entities.GhoulEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BaseGhoulModel extends AnimatedGeoModel<GhoulEntity> {
    @Override
    public Identifier getModelResource(GhoulEntity object) {
        return new Identifier(TheGraveyard.MOD_ID, "geo/ghoul.geo.json");
    }

    @Override
    public Identifier getTextureResource(GhoulEntity object) {
        return new Identifier(TheGraveyard.MOD_ID, "textures/entity/ghoul_skin" + object.getVariant() + ".png");
    }

    @Override
    public Identifier getAnimationResource(GhoulEntity animatable) {
        return new Identifier(TheGraveyard.MOD_ID, "animations/ghoul/ghoul.animation.json");
    }
}
