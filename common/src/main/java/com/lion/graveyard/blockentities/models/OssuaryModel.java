package com.lion.graveyard.blockentities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.blockentities.OssuaryBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class OssuaryModel extends GeoModel<OssuaryBlockEntity> {
    @Override
    public ResourceLocation getAnimationResource(OssuaryBlockEntity entity) {
        return new ResourceLocation(Graveyard.MOD_ID,"animations/ossuary/ossuary.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(OssuaryBlockEntity animatable) {
        return new ResourceLocation(Graveyard.MOD_ID, "geo/ossuary.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(OssuaryBlockEntity entity) {
        return new ResourceLocation(Graveyard.MOD_ID, "textures/block/ossuary.png");
    }
}
