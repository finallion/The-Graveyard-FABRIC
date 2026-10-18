package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.GhoulEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class BaseGhoulModel extends GeoModel<GhoulEntity> {
    private static final DataTicket<Byte> VARIANT = DataTicket.create("graveyard_ghoul_variant", Byte.class);

    @Override
    public ResourceLocation getModelResource(GeoRenderState renderState) {
        return Graveyard.id("ghoul");
    }

    @Override
    public ResourceLocation getTextureResource(GeoRenderState renderState) {
        return Graveyard.id("textures/entity/ghoul_skin" + renderState.getGeckolibData(VARIANT) + ".png");
    }

    @Override
    public void addAdditionalStateData(GhoulEntity animatable, GeoRenderState renderState) {
        renderState.addGeckolibData(VARIANT, animatable.getVariant());
    }

    @Override
    public ResourceLocation getAnimationResource(GhoulEntity animatable) {
        return Graveyard.id("ghoul/ghoul");
    }
}
