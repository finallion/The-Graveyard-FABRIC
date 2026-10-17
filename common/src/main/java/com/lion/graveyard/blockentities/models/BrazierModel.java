package com.lion.graveyard.blockentities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.blockentities.BrazierBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class BrazierModel extends GeoModel<BrazierBlockEntity> {
    @Override
    public Identifier getAnimationResource(BrazierBlockEntity entity) {
        return Graveyard.id("animations/brazier.animation.json");
    }

    @Override
    public Identifier getModelResource(BrazierBlockEntity animatable) {
        return Graveyard.id("geo/brazier.geo.json");
    }

    @Override
    public Identifier getTextureResource(BrazierBlockEntity entity) {
        return Graveyard.id("textures/block/brazier.png");
    }
}
