package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.AnimatedCorruptedIllager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CorruptedIllagerGeoModel<T extends AnimatedCorruptedIllager> extends GeoModel<T> {

    @Override
    public ResourceLocation getModelResource(T entity) {
        return Graveyard.id("geo/corrupted_illager.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(T entity) {
        String type = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).getPath();
        return Graveyard.id("textures/entity/" + type + "_" + entity.getDecay() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(T entity) {
        return Graveyard.id("animations/corrupted_illager/corrupted_illager.animation.json");
    }
}
