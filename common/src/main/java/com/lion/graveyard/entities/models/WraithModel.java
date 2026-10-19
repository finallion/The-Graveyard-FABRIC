package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.WraithEntity;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class WraithModel extends GeoModel<WraithEntity> {

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return Graveyard.id("wraith");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return Graveyard.id("textures/entity/wraith_opaque.png");
    }

    @Override
    public Identifier getAnimationResource(WraithEntity animatable) {
        return Graveyard.id("wraith/wraith");
    }
}
