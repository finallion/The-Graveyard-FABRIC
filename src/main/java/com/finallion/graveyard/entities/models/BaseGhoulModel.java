package com.finallion.graveyard.entities.models;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.entities.GhoulEntity;
import com.finallion.graveyard.entities.GhoulingEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class BaseGhoulModel extends GeoModel<GhoulEntity> {

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
