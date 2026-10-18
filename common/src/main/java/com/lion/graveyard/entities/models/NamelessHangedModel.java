package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.NamelessHangedEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class NamelessHangedModel extends GeoModel<NamelessHangedEntity> {

    @Override
    public ResourceLocation getModelResource(GeoRenderState renderState) {
        return Graveyard.id("nameless_hanged");
    }

    @Override
    public ResourceLocation getTextureResource(GeoRenderState renderState) {
        return Graveyard.id("textures/entity/nameless_hanged.png");
    }

    @Override
    public ResourceLocation getAnimationResource(NamelessHangedEntity animatable) {
        return Graveyard.id("nameless_hanged/nameless_hanged");
    }
}
