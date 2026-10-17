package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.WraithEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class WraithModel extends GeoModel<WraithEntity> {

    @Override
    public ResourceLocation getModelResource(WraithEntity object) {
        return Graveyard.id("geo/wraith.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WraithEntity object) {
        return Graveyard.id("textures/entity/wraith_opaque.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WraithEntity animatable) {
        return Graveyard.id("animations/wraith/wraith.animation.json");
    }
}
