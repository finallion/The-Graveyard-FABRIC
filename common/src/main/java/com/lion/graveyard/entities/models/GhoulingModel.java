package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.client.GraveyardDataTickets;
import com.lion.graveyard.entities.GhoulingEntity;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class GhoulingModel extends GeoModel<GhoulingEntity> {
    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return Graveyard.id("ghouling");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return Graveyard.id("textures/entity/" + textureVariant(renderState.getGeckolibData(GraveyardDataTickets.GHOULING_VARIANT)) + "_ghouling.png");
    }

    @Override
    public void addAdditionalStateData(GhoulingEntity animatable, GeoRenderState renderState) {
        renderState.addGeckolibData(GraveyardDataTickets.GHOULING_VARIANT, animatable.getVariant());
    }

    private static int textureVariant(byte variant) {
        return variant >= 1 && variant <= 5 ? variant : 1;
    }

    @Override
    public Identifier getAnimationResource(GhoulingEntity animatable) {
        return Graveyard.id("ghouling/ghouling");
    }
}
