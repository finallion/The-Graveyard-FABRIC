package com.finallion.graveyard.entities.models;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.entities.GhoulEntity;
import com.finallion.graveyard.entities.ReaperEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ReaperModel extends AnimatedGeoModel<ReaperEntity> {

    @Override
    public Identifier getModelLocation(ReaperEntity object) {
        return new Identifier(TheGraveyard.MOD_ID, "geo/reaper.geo.json");
    }

    @Override
    public Identifier getTextureLocation(ReaperEntity object) {
        return new Identifier(TheGraveyard.MOD_ID, "textures/entity/reaper.png");
    }

    @Override
    public Identifier getAnimationFileLocation(ReaperEntity animatable) {
        return new Identifier(TheGraveyard.MOD_ID, "animations/reaper/reaper.animation.json");
    }


}
