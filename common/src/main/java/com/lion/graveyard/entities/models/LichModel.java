package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.LichEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;


public class LichModel extends GeoModel<LichEntity> {
    private Identifier texture = new Identifier(Graveyard.MOD_ID, "textures/entity/lich_texture.png");

    @Override
    public Identifier getModelResource(LichEntity object) {
        return new Identifier(Graveyard.MOD_ID, "geo/lich.geo.json");
    }

    @Override
    public Identifier getTextureResource(LichEntity object) {
        return texture;
    }

    @Override
    public Identifier getAnimationResource(LichEntity animatable) {
        return new Identifier(Graveyard.MOD_ID, "animations/lich/lich.animation.json");
    }

}
