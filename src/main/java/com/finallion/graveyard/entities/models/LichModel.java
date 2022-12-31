package com.finallion.graveyard.entities.models;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.entities.GhoulEntity;
import com.finallion.graveyard.entities.LichEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;


public class LichModel extends GeoModel<LichEntity> {
    private Identifier texture = new Identifier(TheGraveyard.MOD_ID, "textures/entity/lich_texture.png");

    @Override
    public Identifier getModelResource(LichEntity object) {
        return new Identifier(TheGraveyard.MOD_ID, "geo/lich.geo.json");
    }

    @Override
    public Identifier getTextureResource(LichEntity object) {
        return texture;
    }

    @Override
    public Identifier getAnimationResource(LichEntity animatable) {
        return new Identifier(TheGraveyard.MOD_ID, "animations/lich/lich.animation.json");
    }

}
