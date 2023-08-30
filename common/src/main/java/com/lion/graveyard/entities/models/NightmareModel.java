package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.NightmareEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;


public class NightmareModel extends GeoModel<NightmareEntity> {

    @Override
    public Identifier getModelResource(NightmareEntity object) {
        return new Identifier(Graveyard.MOD_ID, "geo/nightmare.geo.json");
    }

    @Override
    public Identifier getTextureResource(NightmareEntity object) {
        return new Identifier(Graveyard.MOD_ID, "textures/entity/nightmare.png");
    }

    @Override
    public Identifier getAnimationResource(NightmareEntity animatable) {
        return new Identifier(Graveyard.MOD_ID, "animations/nightmare/nightmare.animation.json");
    }
}
