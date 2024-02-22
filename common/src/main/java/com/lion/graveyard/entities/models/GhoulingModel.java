package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.GhoulingEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GhoulingModel extends GeoModel<GhoulingEntity> {
    private ResourceLocation texture = new ResourceLocation(Graveyard.MOD_ID, "textures/entity/white_ghouling.png");

    @Override
    public ResourceLocation getModelResource(GhoulingEntity object) {
        return new ResourceLocation(Graveyard.MOD_ID, "geo/ghouling.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GhoulingEntity object) {
        return new ResourceLocation(Graveyard.MOD_ID, "textures/entity/" + object.getVariant() + "_ghouling.png");
    }

    @Override
    public ResourceLocation getAnimationResource(GhoulingEntity animatable) {
        return new ResourceLocation(Graveyard.MOD_ID, "animations/ghouling/ghouling.animation.json");
    }
}
