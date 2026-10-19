package com.lion.graveyard.blockentities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.blockentities.OssuaryBlockEntity;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class OssuaryModel extends GeoModel<OssuaryBlockEntity> {
    @Override
    public Identifier getAnimationResource(OssuaryBlockEntity entity) {
        return Graveyard.id("ossuary/ossuary");
    }

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return Graveyard.id("ossuary");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return Graveyard.id("textures/block/ossuary.png");
    }
}
