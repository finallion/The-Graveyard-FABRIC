package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.NightmareEntity;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class NightmareModel extends GeoModel<NightmareEntity> {

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return Graveyard.id("nightmare");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return Graveyard.id("textures/entity/nightmare.png");
    }

    @Override
    public Identifier getAnimationResource(NightmareEntity animatable) {
        return Graveyard.id("nightmare/nightmare");
    }
}
