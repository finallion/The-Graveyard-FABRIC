package com.finallion.graveyard.entities.models;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.entities.NightmareEntity;
import com.finallion.graveyard.entities.RevenantEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;


public class NightmareModel extends GeoModel<NightmareEntity> {

    @Override
    public Identifier getModelResource(NightmareEntity object) {
        return new Identifier(TheGraveyard.MOD_ID, "geo/nightmare.geo.json");
    }

    @Override
    public Identifier getTextureResource(NightmareEntity object) {
        return new Identifier(TheGraveyard.MOD_ID, "textures/entity/nightmare.png");
    }

    @Override
    public Identifier getAnimationResource(NightmareEntity animatable) {
        return new Identifier(TheGraveyard.MOD_ID, "animations/nightmare/nightmare.animation.json");
    }
}
