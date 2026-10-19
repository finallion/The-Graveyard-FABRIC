package com.lion.graveyard.entities.models;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.client.GraveyardDataTickets;
import com.lion.graveyard.entities.CorvidEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationProcessor;
import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class CorvidModel extends GeoModel<CorvidEntity> {
    private static final ResourceLocation ANIMATION = Graveyard.id("corvid/corvid");
    private static final float MAX_HEAD_PITCH = 40.0F;
    private static final float MAX_HEAD_YAW = 75.0F;

    @Override
    public ResourceLocation getModelResource(GeoRenderState renderState) {
        return renderState.getGeckolibData(GraveyardDataTickets.CORVID_SPECIES).getModel();
    }

    @Override
    public ResourceLocation getTextureResource(GeoRenderState renderState) {
        return renderState.getGeckolibData(GraveyardDataTickets.CORVID_SPECIES).getTexture();
    }

    @Override
    public ResourceLocation getAnimationResource(CorvidEntity entity) {
        return ANIMATION;
    }

    @Override
    public void addAdditionalStateData(CorvidEntity animatable, GeoRenderState renderState) {
        // The additive look rotation fights the head motion of the peck animation.
        float partialTick = renderState.getGeckolibData(DataTickets.PARTIAL_TICK);
        boolean peck = this.isPlayingPeck(renderState.getGeckolibData(DataTickets.ANIMATABLE_MANAGER));
        renderState.addGeckolibData(GraveyardDataTickets.CORVID_SPECIES, animatable.getSpecies());
        renderState.addGeckolibData(GraveyardDataTickets.CORVID_LOOK_WEIGHT, animatable.stepLookWeight(peck, partialTick));
    }

    @Override
    public void setCustomAnimations(AnimationState<CorvidEntity> animationState) {
        float weight = animationState.getDataOrDefault(GraveyardDataTickets.CORVID_LOOK_WEIGHT, 0.0F);

        if (weight <= 0.0F) {
            return;
        }

        this.getBone("head").ifPresent(head -> {
            float headPitch = animationState.getData(DataTickets.ENTITY_PITCH);
            float headYaw = animationState.getData(DataTickets.ENTITY_YAW);

            head.setRotX(head.getRotX() - Mth.clamp(headPitch, -MAX_HEAD_PITCH, MAX_HEAD_PITCH) * Mth.DEG_TO_RAD * weight);
            head.setRotY(head.getRotY() - Mth.clamp(headYaw, -MAX_HEAD_YAW, MAX_HEAD_YAW) * Mth.DEG_TO_RAD * weight);
        });
    }

    private boolean isPlayingPeck(AnimatableManager<CorvidEntity> manager) {
        AnimationController<CorvidEntity> controller = manager.getAnimationControllers().get("controller");

        if (controller == null) {
            return false;
        }

        AnimationProcessor.QueuedAnimation current = controller.getCurrentAnimation();
        return current != null && current.animation().name().endsWith(".peck");
    }
}
