package com.finallion.graveyard.blockentities.model;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.blockentities.CoffinBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class CoffinModel extends AnimatedGeoModel<CoffinBlockEntity> {

    @Override
    public Identifier getAnimationFileLocation(CoffinBlockEntity animatable) {
        return new Identifier(TheGraveyard.MOD_ID, "animations/sarcophagus/sarcophagus.animation.json");
    }

    @Override
    public Identifier getModelLocation(CoffinBlockEntity animatable) {
        return new Identifier(TheGraveyard.MOD_ID, "geo/sarcophagus.geo.json");
    }

    @Override
    public Identifier getTextureLocation(CoffinBlockEntity entity) {
        return new Identifier(TheGraveyard.MOD_ID, "textures/block/sarcophagus.png");
    }
}