package com.finallion.graveyard.entities.models;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.entities.RevenantEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class RevenantModel extends GeoModel<RevenantEntity> {

    @Override
    public Identifier getModelResource(RevenantEntity object) {
        return new Identifier(TheGraveyard.MOD_ID, "geo/revenant.geo.json");
    }

    @Override
    public Identifier getTextureResource(RevenantEntity object) {
        return new Identifier(TheGraveyard.MOD_ID, "textures/entity/revenant.png");
    }

    @Override
    public Identifier getAnimationResource(RevenantEntity animatable) {
        return new Identifier(TheGraveyard.MOD_ID, "animations/revenant/revenant.animation.json");
    }
}
