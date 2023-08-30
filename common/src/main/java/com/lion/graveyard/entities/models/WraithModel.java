package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.WraithEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class WraithModel extends GeoModel<WraithEntity> {

    @Override
    public Identifier getModelResource(WraithEntity object) {
        return new Identifier(Graveyard.MOD_ID, "geo/wraith.geo.json");
    }

    @Override
    public Identifier getTextureResource(WraithEntity object) {
        return new Identifier(Graveyard.MOD_ID, "textures/entity/wraith_opaque.png");
    }

    @Override
    public Identifier getAnimationResource(WraithEntity animatable) {
        return new Identifier(Graveyard.MOD_ID, "animations/wraith/wraith.animation.json");
    }
}
