package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.CorvidEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationProcessor;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class CorvidModel extends GeoModel<CorvidEntity> {
    private static final Identifier ANIMATION = new Identifier(Graveyard.MOD_ID, "animations/corvid/corvid.animation.json");
    private static final float MAX_HEAD_PITCH = 40.0F;
    private static final float MAX_HEAD_YAW = 75.0F;

    @Override
    public Identifier getModelResource(CorvidEntity entity) {
        return entity.getSpecies().getModel();
    }

    @Override
    public Identifier getTextureResource(CorvidEntity entity) {
        return entity.getSpecies().getTexture();
    }

    @Override
    public Identifier getAnimationResource(CorvidEntity entity) {
        return ANIMATION;
    }

    @Override
    public void setCustomAnimations(CorvidEntity animatable, long instanceId, AnimationState<CorvidEntity> animationState) {
        // The additive look rotation fights the head motion of the peck animation.
        if (this.isPlayingPeck(animatable, instanceId)) {
            return;
        }
        this.getBone("head").ifPresent(head -> {
            EntityModelData data = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            if (data == null) {
                return;
            }
            head.setRotX(head.getRotX() + MathHelper.clamp(data.headPitch(), -MAX_HEAD_PITCH, MAX_HEAD_PITCH) * MathHelper.RADIANS_PER_DEGREE);
            head.setRotY(head.getRotY() + MathHelper.clamp(data.netHeadYaw(), -MAX_HEAD_YAW, MAX_HEAD_YAW) * MathHelper.RADIANS_PER_DEGREE);
        });
    }

    private boolean isPlayingPeck(CorvidEntity animatable, long instanceId) {
        AnimatableManager<CorvidEntity> manager = animatable.getAnimatableInstanceCache().getManagerForId(instanceId);
        AnimationController<CorvidEntity> controller = manager.getAnimationControllers().get("controller");
        if (controller == null) {
            return false;
        }
        AnimationProcessor.QueuedAnimation current = controller.getCurrentAnimation();
        return current != null && current.animation().name().equals("animation.crow.peck");
    }
}
