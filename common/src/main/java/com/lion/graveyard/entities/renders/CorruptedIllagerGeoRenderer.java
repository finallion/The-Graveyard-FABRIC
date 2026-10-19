package com.lion.graveyard.entities.renders;

import com.lion.graveyard.client.GraveyardDataTickets;
import com.lion.graveyard.entities.AnimatedCorruptedIllager;
import com.lion.graveyard.entities.models.CorruptedIllagerGeoModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.BoneSnapshots;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.RenderPassInfo;
import software.bernie.geckolib.renderer.layer.builtin.AutoGlowingGeoLayer;

public class CorruptedIllagerGeoRenderer<T extends AnimatedCorruptedIllager, R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<T, R> {

    public CorruptedIllagerGeoRenderer(EntityRendererProvider.Context context) {
        super(context, new CorruptedIllagerGeoModel<>());
        this.shadowRadius = 0.5F;
        withRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    protected float getDeathMaxRotation(GeoRenderState renderState) {
        return 0.0F;
    }

    @Override
    public void addRenderData(T animatable, Void relatedObject, R renderState, float partialTick) {
        renderState.addGeckolibData(GraveyardDataTickets.ILLAGER_LEFT_CLAW, animatable.hasLeftClaw());
        renderState.addGeckolibData(GraveyardDataTickets.ILLAGER_RIGHT_CLAW, animatable.hasRightClaw());
    }

    @Override
    public void adjustModelBonesForRender(RenderPassInfo<R> renderPassInfo, BoneSnapshots snapshots) {
        R renderState = renderPassInfo.renderState();
        // Illagers share one baked model. Set claw visibility for each entity.
        snapshots.ifPresent("left_claw", claw -> claw.skipRender(!renderState.getGeckolibData(GraveyardDataTickets.ILLAGER_LEFT_CLAW)));
        snapshots.ifPresent("right_claw", claw -> claw.skipRender(!renderState.getGeckolibData(GraveyardDataTickets.ILLAGER_RIGHT_CLAW)));
    }
}
