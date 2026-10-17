package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.AnimatedCorruptedIllager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class CorruptedIllagerGeoModel<T extends AnimatedCorruptedIllager> extends GeoModel<T> {

    @Override
    public Identifier getModelResource(T entity) {
        return new Identifier(Graveyard.MOD_ID, "geo/corrupted_illager.geo.json");
    }

    @Override
    public Identifier getTextureResource(T entity) {
        String type = Registries.ENTITY_TYPE.getId(entity.getType()).getPath();
        return new Identifier(Graveyard.MOD_ID, "textures/entity/" + type + "_" + entity.getDecay() + ".png");
    }

    @Override
    public Identifier getAnimationResource(T entity) {
        return new Identifier(Graveyard.MOD_ID, "animations/corrupted_illager/corrupted_illager.animation.json");
    }
}
