package com.finallion.graveyard.entities.models;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.entities.NightmareEntity;
import com.finallion.graveyard.entities.WraithEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class WraithModel extends GeoModel<WraithEntity> {

    @Override
    public Identifier getModelResource(WraithEntity object) {
        return new Identifier(TheGraveyard.MOD_ID, "geo/wraith.geo.json");
    }

    @Override
    public Identifier getTextureResource(WraithEntity object) {
        return new Identifier(TheGraveyard.MOD_ID, "textures/entity/wraith_opaque.png");
    }

    @Override
    public Identifier getAnimationResource(WraithEntity animatable) {
        return new Identifier(TheGraveyard.MOD_ID, "animations/wraith/wraith.animation.json");
    }
}
