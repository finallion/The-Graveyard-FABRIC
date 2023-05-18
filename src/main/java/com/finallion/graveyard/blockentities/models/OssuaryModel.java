package com.finallion.graveyard.blockentities.models;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.blockentities.BrazierBlockEntity;
import com.finallion.graveyard.blockentities.OssuaryBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;


public class OssuaryModel extends AnimatedGeoModel<OssuaryBlockEntity> {
    @Override
    public Identifier getAnimationResource(OssuaryBlockEntity entity) {
        return new Identifier(TheGraveyard.MOD_ID,"animations/ossuary/ossuary.animation.json");
    }

    @Override
    public Identifier getModelResource(OssuaryBlockEntity animatable) {
        return new Identifier(TheGraveyard.MOD_ID, "geo/ossuary.geo.json");
    }

    @Override
    public Identifier getTextureResource(OssuaryBlockEntity entity) {
        return new Identifier(TheGraveyard.MOD_ID, "textures/block/ossuary.png");
    }




}
