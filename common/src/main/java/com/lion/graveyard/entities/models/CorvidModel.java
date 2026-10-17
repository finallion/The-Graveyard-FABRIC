package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.CorvidEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationProcessor;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class CorvidModel extends GeoModel<CorvidEntity> {
    private static final ResourceLocation ANIMATION = Graveyard.id("animations/corvid/corvid.animation.json");
    private static final float MAX_HEAD_PITCH = 40.0F;
    private static final float MAX_HEAD_YAW = 75.0F;

    @Override
    public ResourceLocation getModelResource(CorvidEntity entity) {
        return entity.getSpecies().getModel();
    }

    @Override
    public ResourceLocation getTextureResource(CorvidEntity entity) {
        return entity.getSpecies().getTexture();
    }

    @Override
    public ResourceLocation getAnimationResource(CorvidEntity entity) {
        return ANIMATION;
    }

    @Override
    public void setCustomAnimations(CorvidEntity animatable, long instanceId, AnimationState<CorvidEntity> animationState) {
        // The additive look rotation fights the head motion of the peck animation.
        float weight = animatable.stepLookWeight(this.isPlayingPeck(animatable, instanceId), animationState.getPartialTick());

        if (weight <= 0.0F) {
            return;
        }

        this.getBone("head").ifPresent(head -> {
            EntityModelData data = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            if (data == null) {
                return;
            }

            head.setRotX(head.getRotX() + Mth.clamp(data.headPitch(), -MAX_HEAD_PITCH, MAX_HEAD_PITCH) * Mth.DEG_TO_RAD * weight);
            head.setRotY(head.getRotY() + Mth.clamp(data.netHeadYaw(), -MAX_HEAD_YAW, MAX_HEAD_YAW) * Mth.DEG_TO_RAD * weight);
        });
    }

    private boolean isPlayingPeck(CorvidEntity animatable, long instanceId) {
        AnimatableManager<CorvidEntity> manager = animatable.getAnimatableInstanceCache().getManagerForId(instanceId);
        AnimationController<CorvidEntity> controller = manager.getAnimationControllers().get("controller");

        if (controller == null) {
            return false;
        }

        AnimationProcessor.QueuedAnimation current = controller.getCurrentAnimation();
        return current != null && current.animation().name().endsWith(".peck");
    }
}
