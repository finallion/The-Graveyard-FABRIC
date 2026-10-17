package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.RevenantEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RevenantModel extends GeoModel<RevenantEntity> {

    @Override
    public ResourceLocation getModelResource(RevenantEntity object) {
        return Graveyard.id("geo/revenant.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(RevenantEntity object) {
        return Graveyard.id("textures/entity/revenant.png");
    }

    @Override
    public ResourceLocation getAnimationResource(RevenantEntity animatable) {
        return Graveyard.id("animations/revenant/revenant.animation.json");
    }
}
