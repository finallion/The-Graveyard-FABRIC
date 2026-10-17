package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.LichEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class LichModel extends GeoModel<LichEntity> {
    private static final float MAX_HEAD_YAW = 50.0F;
    private Identifier texture = Graveyard.id("textures/entity/lich_texture.png");

    @Override
    public Identifier getModelResource(LichEntity object) {
        return Graveyard.id("geo/lich.geo.json");
    }

    @Override
    public Identifier getTextureResource(LichEntity object) {
        return texture;
    }

    @Override
    public Identifier getAnimationResource(LichEntity animatable) {
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

            head.setRotY(head.getRotY() + MathHelper.clamp(data.netHeadYaw(), -MAX_HEAD_YAW, MAX_HEAD_YAW) * MathHelper.RADIANS_PER_DEGREE);
        });
    }
}
