package com.finallion.graveyard.entities.models;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.entities.BaseGhoulEntity;
import com.finallion.graveyard.entities.ReaperEntity;
import com.finallion.graveyard.entities.RevenantEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class RevenantModel extends AnimatedGeoModel<RevenantEntity> {

    @Override
    public Identifier getModelLocation(RevenantEntity object) {
        return new Identifier(TheGraveyard.MOD_ID, "geo/revenant.geo.json");
    }

    @Override
    public Identifier getTextureLocation(RevenantEntity object) {
        return new Identifier(TheGraveyard.MOD_ID, "textures/entity/revenant.png");
    }

    @Override
    public Identifier getAnimationFileLocation(RevenantEntity animatable) {
        return new Identifier(TheGraveyard.MOD_ID, "animations/revenant/revenant.animation.json");
    }
}
