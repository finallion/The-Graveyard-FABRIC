package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.ReaperEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class ReaperModel extends GeoModel<ReaperEntity> {

    @Override
    public ResourceLocation getModelResource(GeoRenderState renderState) {
        return Graveyard.id("reaper");
    }

    @Override
    public ResourceLocation getTextureResource(GeoRenderState renderState) {
        return Graveyard.id("textures/entity/reaper.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ReaperEntity animatable) {
        return Graveyard.id("reaper/reaper");
    }


}
