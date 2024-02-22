package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.ReaperEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ReaperModel extends GeoModel<ReaperEntity> {

    @Override
    public ResourceLocation getModelResource(ReaperEntity object) {
        return new ResourceLocation(Graveyard.MOD_ID, "geo/reaper.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ReaperEntity object) {
        return new ResourceLocation(Graveyard.MOD_ID, "textures/entity/reaper.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ReaperEntity animatable) {
        return new ResourceLocation(Graveyard.MOD_ID, "animations/reaper/reaper.animation.json");
    }


}
