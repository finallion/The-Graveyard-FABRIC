package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.FallingCorpse;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FallingCorpseModel extends GeoModel<FallingCorpse> {

    @Override
    public ResourceLocation getModelResource(FallingCorpse object) {
        return new ResourceLocation(Graveyard.MOD_ID, "geo/falling_corpse.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(FallingCorpse object) {
        return new ResourceLocation("textures/entity/skeleton/stray.png");
    }

    @Override
    public ResourceLocation getAnimationResource(FallingCorpse animatable) {
        return new ResourceLocation(Graveyard.MOD_ID, "animations/falling_corpse/falling_corpse.animation.json");
    }
}
