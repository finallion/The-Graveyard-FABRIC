package com.lion.graveyard.blockentities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.blockentities.OssuaryBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class OssuaryModel extends GeoModel<OssuaryBlockEntity> {
    @Override
    public ResourceLocation getAnimationResource(OssuaryBlockEntity entity) {
        return Graveyard.id("animations/ossuary/ossuary.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(OssuaryBlockEntity animatable) {
        return Graveyard.id("geo/ossuary.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(OssuaryBlockEntity entity) {
        return Graveyard.id("textures/block/ossuary.png");
    }
}
