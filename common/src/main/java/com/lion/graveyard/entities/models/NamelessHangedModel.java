package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.NamelessHangedEntity;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class NamelessHangedModel extends GeoModel<NamelessHangedEntity> {

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return Graveyard.id("nameless_hanged");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return Graveyard.id("textures/entity/nameless_hanged.png");
    }

    @Override
    public Identifier getAnimationResource(NamelessHangedEntity animatable) {
        return Graveyard.id("nameless_hanged/nameless_hanged");
    }
}
