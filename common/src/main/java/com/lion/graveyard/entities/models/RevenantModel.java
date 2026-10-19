package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.RevenantEntity;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class RevenantModel extends GeoModel<RevenantEntity> {

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return Graveyard.id("revenant");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return Graveyard.id("textures/entity/revenant.png");
    }

    @Override
    public Identifier getAnimationResource(RevenantEntity animatable) {
        return Graveyard.id("revenant/revenant");
    }
}
