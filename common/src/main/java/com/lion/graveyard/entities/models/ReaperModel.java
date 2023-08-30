package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.ReaperEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class ReaperModel extends GeoModel<ReaperEntity> {

    @Override
    public Identifier getModelResource(ReaperEntity object) {
        return new Identifier(Graveyard.MOD_ID, "geo/reaper.geo.json");
    }

    @Override
    public Identifier getTextureResource(ReaperEntity object) {
        return new Identifier(Graveyard.MOD_ID, "textures/entity/reaper.png");
    }

    @Override
    public Identifier getAnimationResource(ReaperEntity animatable) {
        return new Identifier(Graveyard.MOD_ID, "animations/reaper/reaper.animation.json");
    }


}
