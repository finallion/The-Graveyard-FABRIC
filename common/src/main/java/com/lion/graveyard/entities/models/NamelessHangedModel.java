package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.NamelessHangedEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class NamelessHangedModel extends GeoModel<NamelessHangedEntity> {

    @Override
    public ResourceLocation getModelResource(NamelessHangedEntity object) {
        return new ResourceLocation(Graveyard.MOD_ID, "geo/nameless_hanged.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(NamelessHangedEntity object) {
        return new ResourceLocation(Graveyard.MOD_ID,"textures/entity/nameless_hanged.png");
    }

    @Override
    public ResourceLocation getAnimationResource(NamelessHangedEntity animatable) {
        return new ResourceLocation(Graveyard.MOD_ID, "animations/nameless_hanged/nameless_hanged.animation.json");
    }
}
