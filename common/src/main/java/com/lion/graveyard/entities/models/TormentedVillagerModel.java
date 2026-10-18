package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.TormentedVillagerEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class TormentedVillagerModel extends GeoModel<TormentedVillagerEntity> {

    @Override
    public ResourceLocation getModelResource(GeoRenderState renderState) {
        return Graveyard.id("tormented_villager");
    }

    @Override
    public ResourceLocation getTextureResource(GeoRenderState renderState) {
        return Graveyard.id("textures/entity/tormented_villager.png");
    }

    @Override
    public ResourceLocation getAnimationResource(TormentedVillagerEntity entity) {
        return Graveyard.id("tormented_villager/tormented_villager");
    }
}
