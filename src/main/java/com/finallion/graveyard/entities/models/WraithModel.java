package com.finallion.graveyard.entities.models;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.entities.NightmareEntity;
import com.finallion.graveyard.entities.WraithEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class WraithModel extends AnimatedGeoModel<WraithEntity> {

    @Override
    public Identifier getModelLocation(WraithEntity object) {
        return new Identifier(TheGraveyard.MOD_ID, "geo/wraith.geo.json");
    }

    @Override
    public Identifier getTextureLocation(WraithEntity object) {
        return new Identifier(TheGraveyard.MOD_ID, "textures/entity/wraith_opaque.png");
    }

    @Override
    public Identifier getAnimationFileLocation(WraithEntity animatable) {
        return new Identifier(TheGraveyard.MOD_ID, "animations/wraith/wraith.animation.json");
    }
}
