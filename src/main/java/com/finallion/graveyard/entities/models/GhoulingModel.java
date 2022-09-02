package com.finallion.graveyard.entities.models;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.entities.GhoulingEntity;
import com.finallion.graveyard.entities.LichEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class GhoulingModel extends AnimatedGeoModel<GhoulingEntity> {
    private Identifier texture = new Identifier(TheGraveyard.MOD_ID, "textures/entity/ghouling.png");

    @Override
    public Identifier getModelResource(GhoulingEntity object) {
        return new Identifier(TheGraveyard.MOD_ID, "geo/ghouling.geo.json");
    }

    @Override
    public Identifier getTextureResource(GhoulingEntity object) {
        return texture;
    }

    @Override
    public Identifier getAnimationResource(GhoulingEntity animatable) {
        return new Identifier(TheGraveyard.MOD_ID, "animations/ghouling/ghouling.animation.json");
    }

}
