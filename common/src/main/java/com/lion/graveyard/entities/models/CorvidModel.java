package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.CorvidEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class CorvidModel extends GeoModel<CorvidEntity> {
    private static final Identifier MODEL = new Identifier(Graveyard.MOD_ID, "geo/corvid.geo.json");
    private static final Identifier ANIMATION = new Identifier(Graveyard.MOD_ID, "animations/corvid/corvid.animation.json");
    private static final Identifier RAVEN = new Identifier(Graveyard.MOD_ID, "textures/entity/raven.png");
    private static final Identifier MAGPIE = new Identifier(Graveyard.MOD_ID, "textures/entity/magpie.png");

    @Override
    public Identifier getModelResource(CorvidEntity entity) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(CorvidEntity entity) {
        return entity.isMagpie() ? MAGPIE : RAVEN;
    }

    @Override
    public Identifier getAnimationResource(CorvidEntity entity) {
        return ANIMATION;
    }
}
