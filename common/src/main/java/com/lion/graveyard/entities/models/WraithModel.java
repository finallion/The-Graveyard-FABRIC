package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.WraithEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class WraithModel extends GeoModel<WraithEntity> {

    @Override
    public Identifier getModelResource(WraithEntity object) {
        return Graveyard.id("geo/wraith.geo.json");
    }

    @Override
    public Identifier getTextureResource(WraithEntity object) {
        return Graveyard.id("textures/entity/wraith_opaque.png");
    }

    @Override
    public Identifier getAnimationResource(WraithEntity animatable) {
        return Graveyard.id("animations/wraith/wraith.animation.json");
    }
}
