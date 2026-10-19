package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.client.GraveyardDataTickets;
import com.lion.graveyard.entities.AnimatedCorruptedIllager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class CorruptedIllagerGeoModel<T extends AnimatedCorruptedIllager> extends GeoModel<T> {
    @Override
    public ResourceLocation getModelResource(GeoRenderState renderState) {
        return Graveyard.id("corrupted_illager");
    }

    @Override
    public ResourceLocation getTextureResource(GeoRenderState renderState) {
        return renderState.getGeckolibData(GraveyardDataTickets.ILLAGER_TEXTURE);
    }

    @Override
    public void addAdditionalStateData(T animatable, GeoRenderState renderState) {
        String type = BuiltInRegistries.ENTITY_TYPE.getKey(animatable.getType()).getPath();
        renderState.addGeckolibData(GraveyardDataTickets.ILLAGER_TEXTURE, Graveyard.id("textures/entity/" + type + "_" + animatable.getDecay() + ".png"));
    }

    @Override
    public ResourceLocation getAnimationResource(T entity) {
        return Graveyard.id("corrupted_illager/corrupted_illager");
    }
}
