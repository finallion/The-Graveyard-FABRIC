package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.FallingCorpse;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class FallingCorpseModel extends GeoModel<FallingCorpse> {

    @Override
    public ResourceLocation getModelResource(GeoRenderState renderState) {
        return Graveyard.id("falling_corpse");
    }

    @Override
    public ResourceLocation getTextureResource(GeoRenderState renderState) {
        return ResourceLocation.withDefaultNamespace("textures/entity/skeleton/stray.png");
    }

    @Override
    public ResourceLocation getAnimationResource(FallingCorpse animatable) {
        return Graveyard.id("falling_corpse/falling_corpse");
    }
}
