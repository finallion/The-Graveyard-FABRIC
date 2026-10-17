package com.lion.graveyard.blockentities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.blockentities.OssuaryBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class OssuaryModel extends GeoModel<OssuaryBlockEntity> {
    @Override
    public Identifier getAnimationResource(OssuaryBlockEntity entity) {
        return Graveyard.id("animations/ossuary/ossuary.animation.json");
    }

    @Override
    public Identifier getModelResource(OssuaryBlockEntity animatable) {
        return Graveyard.id("geo/ossuary.geo.json");
    }

    @Override
    public Identifier getTextureResource(OssuaryBlockEntity entity) {
        return Graveyard.id("textures/block/ossuary.png");
    }
}
