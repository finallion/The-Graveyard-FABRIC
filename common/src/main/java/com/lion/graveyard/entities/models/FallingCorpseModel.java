package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.FallingCorpse;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class FallingCorpseModel extends GeoModel<FallingCorpse> {

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return Graveyard.id("falling_corpse");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return Identifier.withDefaultNamespace("textures/entity/skeleton/stray.png");
    }

    @Override
    public Identifier getAnimationResource(FallingCorpse animatable) {
        return Graveyard.id("falling_corpse/falling_corpse");
    }
}
