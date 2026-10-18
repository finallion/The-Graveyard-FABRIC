package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.GhoulingEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class GhoulingModel extends GeoModel<GhoulingEntity> {
    private static final DataTicket<Byte> VARIANT = DataTicket.create("graveyard_ghouling_variant", Byte.class);

    @Override
    public ResourceLocation getModelResource(GeoRenderState renderState) {
        return Graveyard.id("ghouling");
    }

    @Override
    public ResourceLocation getTextureResource(GeoRenderState renderState) {
        return Graveyard.id("textures/entity/" + textureVariant(renderState.getGeckolibData(VARIANT)) + "_ghouling.png");
    }

    @Override
    public void addAdditionalStateData(GhoulingEntity animatable, GeoRenderState renderState) {
        renderState.addGeckolibData(VARIANT, animatable.getVariant());
    }

    private static int textureVariant(byte variant) {
        return variant >= 1 && variant <= 5 ? variant : 1;
    }

    @Override
    public ResourceLocation getAnimationResource(GhoulingEntity animatable) {
        return Graveyard.id("ghouling/ghouling");
    }
}
