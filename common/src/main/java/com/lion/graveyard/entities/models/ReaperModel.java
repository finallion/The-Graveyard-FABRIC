package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.ReaperEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class ReaperModel extends GeoModel<ReaperEntity> {

    @Override
    public Identifier getModelResource(ReaperEntity object) {
        return Graveyard.id("geo/reaper.geo.json");
    }

    @Override
    public Identifier getTextureResource(ReaperEntity object) {
        return Graveyard.id("textures/entity/reaper.png");
    }

    @Override
    public Identifier getAnimationResource(ReaperEntity animatable) {
        return Graveyard.id("animations/reaper/reaper.animation.json");
    }


}
