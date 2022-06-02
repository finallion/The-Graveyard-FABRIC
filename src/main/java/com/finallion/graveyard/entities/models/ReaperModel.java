package com.finallion.graveyard.entities.models;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.entities.ReaperEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ReaperModel extends AnimatedGeoModel<ReaperEntity> {

    @Override
    public Identifier getModelResource(ReaperEntity object) {
        return new Identifier(TheGraveyard.MOD_ID, "geo/reaper.geo.json");
    }

    @Override
    public Identifier getTextureResource(ReaperEntity object) {
        return new Identifier(TheGraveyard.MOD_ID, "textures/entity/reaper.png");
    }

    @Override
    public Identifier getAnimationResource(ReaperEntity animatable) {
        return new Identifier(TheGraveyard.MOD_ID, "animations/reaper/reaper.animation.json");
    }


}
