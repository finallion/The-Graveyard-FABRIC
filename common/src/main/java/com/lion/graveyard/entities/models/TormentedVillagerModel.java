package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.TormentedVillagerEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TormentedVillagerModel extends GeoModel<TormentedVillagerEntity> {

    @Override
    public ResourceLocation getModelResource(TormentedVillagerEntity entity) {
        return Graveyard.id("geo/tormented_villager.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TormentedVillagerEntity entity) {
        return Graveyard.id("textures/entity/tormented_villager.png");
    }

    @Override
    public ResourceLocation getAnimationResource(TormentedVillagerEntity entity) {
        return Graveyard.id("animations/tormented_villager/tormented_villager.animation.json");
    }
}
