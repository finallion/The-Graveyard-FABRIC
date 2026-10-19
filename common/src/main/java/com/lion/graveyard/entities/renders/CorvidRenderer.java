package com.lion.graveyard.entities.renders;

import com.lion.graveyard.client.GraveyardDataTickets;
import com.lion.graveyard.entities.CorvidEntity;
import com.lion.graveyard.entities.models.CorvidModel;
import com.lion.graveyard.entities.renders.features.CorvidHeldItemLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.BoneSnapshots;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.RenderPassInfo;

public class CorvidRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<CorvidEntity, R> {
    private static final float MAX_HEAD_PITCH = 40.0F;
    private static final float MAX_HEAD_YAW = 75.0F;

    public CorvidRenderer(EntityRendererProvider.Context context) {
        super(context, new CorvidModel());
        this.shadowRadius = 0.2F;
        this.withRenderLayer(new CorvidHeldItemLayer<>(this));
    }

    @Override
    protected float getDeathMaxRotation(GeoRenderState renderState) {
        // The death animation lays the bird down.
        return 0.0F;
    }

    @Override
    public void adjustModelBonesForRender(RenderPassInfo<R> renderPassInfo, BoneSnapshots snapshots) {
        R renderState = renderPassInfo.renderState();
        float weight = renderState.getOrDefaultGeckolibData(GraveyardDataTickets.CORVID_LOOK_WEIGHT, 0.0F);

        if (weight <= 0.0F) {
            return;
        }

        float headPitch = renderState.getOrDefaultGeckolibData(DataTickets.ENTITY_PITCH, 0.0F);
        float headYaw = renderState.getOrDefaultGeckolibData(DataTickets.ENTITY_YAW, 0.0F);

        snapshots.ifPresent("head", head -> {
            head.setRotX(head.getRotX() - Mth.clamp(headPitch, -MAX_HEAD_PITCH, MAX_HEAD_PITCH) * Mth.DEG_TO_RAD * weight);
            head.setRotY(head.getRotY() - Mth.clamp(headYaw, -MAX_HEAD_YAW, MAX_HEAD_YAW) * Mth.DEG_TO_RAD * weight);
        });
    }

}
