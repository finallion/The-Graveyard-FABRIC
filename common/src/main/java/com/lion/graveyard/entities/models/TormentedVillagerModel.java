package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.TormentedVillagerEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class TormentedVillagerModel extends GeoModel<TormentedVillagerEntity> {

    @Override
    public Identifier getModelResource(TormentedVillagerEntity entity) {
        return new Identifier(Graveyard.MOD_ID, "geo/tormented_villager.geo.json");
    }

    @Override
    public Identifier getTextureResource(TormentedVillagerEntity entity) {
        return new Identifier(Graveyard.MOD_ID, "textures/entity/tormented_villager.png");
    }

    @Override
    public Identifier getAnimationResource(TormentedVillagerEntity entity) {
        return new Identifier(Graveyard.MOD_ID, "animations/tormented_villager/tormented_villager.animation.json");
    }
}
