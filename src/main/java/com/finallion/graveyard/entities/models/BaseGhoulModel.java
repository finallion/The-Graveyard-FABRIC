package com.finallion.graveyard.entities.models;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.entities.GhoulEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BaseGhoulModel extends AnimatedGeoModel<GhoulEntity> {

    @Override
    public Identifier getModelLocation(GhoulEntity object) {
        return new Identifier(TheGraveyard.MOD_ID, "geo/decaying_ghoul.geo.json");
    }

    @Override
    public Identifier getTextureLocation(GhoulEntity object) {
        return new Identifier(TheGraveyard.MOD_ID, "decaying_ghoul.png");
    }

    @Override
    public Identifier getAnimationFileLocation(GhoulEntity animatable) {
        return new Identifier(TheGraveyard.MOD_ID, "animations/ghoul/ghoul.animation.json");
    }
}
