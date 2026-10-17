package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.GhoulingEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class GhoulingModel extends GeoModel<GhoulingEntity> {
    @Override
    public Identifier getModelResource(GhoulingEntity object) {
        return Graveyard.id("geo/ghouling.geo.json");
    }

    @Override
    public Identifier getTextureResource(GhoulingEntity object) {
        return Graveyard.id("textures/entity/" + textureVariant(object.getVariant()) + "_ghouling.png");
    }

    private static int textureVariant(byte variant) {
        return variant >= 1 && variant <= 5 ? variant : 1;
    }

    @Override
    public Identifier getAnimationResource(GhoulingEntity animatable) {
        return Graveyard.id("animations/ghouling/ghouling.animation.json");
    }
}
