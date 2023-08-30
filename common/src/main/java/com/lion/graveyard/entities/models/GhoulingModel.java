package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.GhoulingEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class GhoulingModel extends GeoModel<GhoulingEntity> {
    private Identifier texture = new Identifier(Graveyard.MOD_ID, "textures/entity/white_ghouling.png");

    @Override
    public Identifier getModelResource(GhoulingEntity object) {
        return new Identifier(Graveyard.MOD_ID, "geo/ghouling.geo.json");
    }

    @Override
    public Identifier getTextureResource(GhoulingEntity object) {
        return new Identifier(Graveyard.MOD_ID, "textures/entity/" + object.getVariant() + "_ghouling.png");
    }

    @Override
    public Identifier getAnimationResource(GhoulingEntity animatable) {
        return new Identifier(Graveyard.MOD_ID, "animations/ghouling/ghouling.animation.json");
    }
}
