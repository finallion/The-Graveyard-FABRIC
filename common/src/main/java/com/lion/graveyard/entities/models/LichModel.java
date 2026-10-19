package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.client.GraveyardDataTickets;
import com.lion.graveyard.entities.LichEntity;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class LichModel extends GeoModel<LichEntity> {
    private Identifier texture = Graveyard.id("textures/entity/lich_texture.png");

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return Graveyard.id("lich");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return texture;
    }

    @Override
    public Identifier getAnimationResource(LichEntity animatable) {
        return Graveyard.id("lich/lich");
    }

    @Override
    public void addAdditionalStateData(
            LichEntity animatable,
            @Nullable Object relatedObject,
            GeoRenderState renderState
    ) {
        renderState.addGeckolibData(GraveyardDataTickets.LICH_PHASE, animatable.getPhase());
    }

}
