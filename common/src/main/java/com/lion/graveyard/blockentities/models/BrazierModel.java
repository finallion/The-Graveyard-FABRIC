package com.lion.graveyard.blockentities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.blockentities.BrazierBlockEntity;
import net.minecraft.resources.ResourceLocation;

import software.bernie.geckolib.model.GeoModel;


public class BrazierModel extends GeoModel<BrazierBlockEntity> {
    @Override
    public ResourceLocation getAnimationResource(BrazierBlockEntity entity) {
        return new ResourceLocation(Graveyard.MOD_ID,"animations/brazier.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(BrazierBlockEntity animatable) {
        return new ResourceLocation(Graveyard.MOD_ID, "geo/brazier.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BrazierBlockEntity entity) {
        return new ResourceLocation(Graveyard.MOD_ID, "textures/block/brazier.png");
    }
}
