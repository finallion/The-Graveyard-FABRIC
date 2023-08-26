package com.lion.graveyard.blockentities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.blockentities.OssuaryBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;


public class OssuaryModel extends GeoModel<OssuaryBlockEntity> {
    @Override
    public Identifier getAnimationResource(OssuaryBlockEntity entity) {
        return new Identifier(Graveyard.MOD_ID,"animations/ossuary/ossuary.animation.json");
    }

    @Override
    public Identifier getModelResource(OssuaryBlockEntity animatable) {
        return new Identifier(Graveyard.MOD_ID, "geo/ossuary.geo.json");
    }

    @Override
    public Identifier getTextureResource(OssuaryBlockEntity entity) {
        return new Identifier(Graveyard.MOD_ID, "textures/block/ossuary.png");
    }




}
