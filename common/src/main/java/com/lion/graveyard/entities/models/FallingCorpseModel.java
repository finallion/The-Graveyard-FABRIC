package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.FallingCorpse;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FallingCorpseModel extends GeoModel<FallingCorpse> {

    @Override
    public ResourceLocation getModelResource(FallingCorpse object) {
        return Graveyard.id("geo/falling_corpse.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(FallingCorpse object) {
        return ResourceLocation.withDefaultNamespace("textures/entity/skeleton/stray.png");
    }

    @Override
    public ResourceLocation getAnimationResource(FallingCorpse animatable) {
        return Graveyard.id("animations/falling_corpse/falling_corpse.animation.json");
    }
}
