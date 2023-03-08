package com.finallion.graveyard.entities.models;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.entities.FallingCorpse;
import com.finallion.graveyard.entities.NamelessHangedEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class NamelessHangedModel extends GeoModel<NamelessHangedEntity> {

    @Override
    public Identifier getModelResource(NamelessHangedEntity object) {
        return new Identifier(TheGraveyard.MOD_ID, "geo/nameless_hanged.geo.json");
    }

    @Override
    public Identifier getTextureResource(NamelessHangedEntity object) {
        return new Identifier(TheGraveyard.MOD_ID,"textures/entity/nameless_hanged.png");
    }

    @Override
    public Identifier getAnimationResource(NamelessHangedEntity animatable) {
        return new Identifier(TheGraveyard.MOD_ID, "animations/nameless_hanged/nameless_hanged.animation.json");
    }
}
