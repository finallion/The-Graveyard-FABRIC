package com.lion.graveyard.blockentities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.blockentities.BrazierBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class BrazierModel extends GeoModel<BrazierBlockEntity> {
    @Override
    public ResourceLocation getAnimationResource(BrazierBlockEntity entity) {
        return Graveyard.id("brazier");
    }

    @Override
    public ResourceLocation getModelResource(GeoRenderState renderState) {
        return Graveyard.id("brazier");
    }

    @Override
    public ResourceLocation getTextureResource(GeoRenderState renderState) {
        return Graveyard.id("textures/block/brazier.png");
    }
}
