package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.client.GraveyardDataTickets;
import com.lion.graveyard.entities.LichEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class LichModel extends GeoModel<LichEntity> {
    private static final float MAX_HEAD_YAW = 50.0F;
    private ResourceLocation texture = Graveyard.id("textures/entity/lich_texture.png");

    @Override
    public ResourceLocation getModelResource(GeoRenderState renderState) {
        return Graveyard.id("lich");
    }

    @Override
    public ResourceLocation getTextureResource(GeoRenderState renderState) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(LichEntity animatable) {
        return Graveyard.id("lich/lich");
    }

    @Override
    public void addAdditionalStateData(LichEntity animatable, GeoRenderState renderState) {
        renderState.addGeckolibData(GraveyardDataTickets.LICH_PHASE, animatable.getPhase());
    }

    @Override
    public void setCustomAnimations(AnimationState<LichEntity> animationState) {
        if (animationState.getDataOrDefault(GraveyardDataTickets.LICH_PHASE, 0) != 5) {
            return;
        }

        this.getBone("head").ifPresent(head -> {
            float headYaw = animationState.getData(DataTickets.ENTITY_YAW);

            head.setRotY(head.getRotY() - Mth.clamp(headYaw, -MAX_HEAD_YAW, MAX_HEAD_YAW) * Mth.DEG_TO_RAD);
        });
    }
}
