package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.LichEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class LichModel extends GeoModel<LichEntity> {
    private static final float MAX_HEAD_YAW = 50.0F;
    private ResourceLocation texture = Graveyard.id("textures/entity/lich_texture.png");

    @Override
    public ResourceLocation getModelResource(LichEntity object) {
        return Graveyard.id("geo/lich.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LichEntity object) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(LichEntity animatable) {
        return Graveyard.id("animations/lich/lich.animation.json");
    }

    @Override
    public void setCustomAnimations(LichEntity animatable, long instanceId, AnimationState<LichEntity> animationState) {
        if (animatable.getPhase() != 5) {
            return;
        }

        this.getBone("head").ifPresent(head -> {
            EntityModelData data = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            if (data == null) {
                return;
            }

            head.setRotY(head.getRotY() + Mth.clamp(data.netHeadYaw(), -MAX_HEAD_YAW, MAX_HEAD_YAW) * Mth.DEG_TO_RAD);
        });
    }
}
