package com.finallion.graveyard.entities.models;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.entities.FallingCorpse;
import com.finallion.graveyard.entities.NightmareEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class FallingCorpseModel extends GeoModel<FallingCorpse> {

    @Override
    public Identifier getModelResource(FallingCorpse object) {
        return new Identifier(TheGraveyard.MOD_ID, "geo/falling_corpse.geo.json");
    }

    @Override
    public Identifier getTextureResource(FallingCorpse object) {
        return new Identifier("textures/entity/skeleton/stray.png");
    }

    @Override
    public Identifier getAnimationResource(FallingCorpse animatable) {
        return new Identifier(TheGraveyard.MOD_ID, "animations/falling_corpse/falling_corpse.animation.json");
    }
}
