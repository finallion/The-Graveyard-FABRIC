package com.finallion.graveyard.blockentities.models;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.blockentities.BrazierBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;


public class BrazierModel extends GeoModel<BrazierBlockEntity> {
    @Override
    public Identifier getAnimationResource(BrazierBlockEntity entity) {
        return new Identifier(TheGraveyard.MOD_ID,"animations/brazier.animation.json");
    }

    @Override
    public Identifier getModelResource(BrazierBlockEntity animatable) {
        return new Identifier(TheGraveyard.MOD_ID, "geo/brazier.geo.json");
    }

    @Override
    public Identifier getTextureResource(BrazierBlockEntity entity) {
        return new Identifier(TheGraveyard.MOD_ID, "textures/block/brazier.png");
    }
}
