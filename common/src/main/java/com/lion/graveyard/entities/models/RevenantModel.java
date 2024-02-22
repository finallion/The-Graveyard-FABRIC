package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.RevenantEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;


public class RevenantModel extends GeoModel<RevenantEntity> {

    @Override
    public ResourceLocation getModelResource(RevenantEntity object) {
        return new ResourceLocation(Graveyard.MOD_ID, "geo/revenant.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(RevenantEntity object) {
        return new ResourceLocation(Graveyard.MOD_ID, "textures/entity/revenant.png");
    }

    @Override
    public ResourceLocation getAnimationResource(RevenantEntity animatable) {
        return new ResourceLocation(Graveyard.MOD_ID, "animations/revenant/revenant.animation.json");
    }
}
