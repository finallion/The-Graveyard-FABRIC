package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.client.GraveyardDataTickets;
import com.lion.graveyard.entities.AnimatedCorruptedIllager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class CorruptedIllagerGeoModel<T extends AnimatedCorruptedIllager> extends GeoModel<T> {
    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return Graveyard.id("corrupted_illager");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return renderState.getGeckolibData(GraveyardDataTickets.ILLAGER_TEXTURE);
    }

    @Override
    public void addAdditionalStateData(T animatable, @Nullable Object relatedObject, GeoRenderState renderState) {
        String type = BuiltInRegistries.ENTITY_TYPE.getKey(animatable.getType()).getPath();
        renderState.addGeckolibData(GraveyardDataTickets.ILLAGER_TEXTURE, Graveyard.id("textures/entity/" + type + "_" + animatable.getDecay() + ".png"));
    }

    @Override
    public Identifier getAnimationResource(T entity) {
        return Graveyard.id("corrupted_illager/corrupted_illager");
    }
}
