package com.lion.graveyard.entities.renders;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.client.GraveyardDataTickets;
import com.lion.graveyard.entities.LichEntity;
import com.lion.graveyard.entities.models.LichModel;
import com.lion.graveyard.entities.renders.features.GraveyardEyesLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.BoneSnapshots;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.RenderPassInfo;

public class LichRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<LichEntity, R> {
    private static final Identifier EYES = Graveyard.id("textures/entity/lich_eye_texture.png");
    private static final float MAX_HEAD_YAW = 50.0F;

    public LichRenderer(EntityRendererProvider.Context context) {
        super(context, new LichModel());
        this.shadowRadius = 1.0F;
        this.withRenderLayer(new GraveyardEyesLayer<>(this, EYES));
    }

    // stops the vanilla death animation
    @Override
    protected float getDeathMaxRotation(GeoRenderState renderState) {
        return 0.0F;
    }

    @Override
    public void adjustModelBonesForRender(RenderPassInfo<R> renderPassInfo, BoneSnapshots snapshots) {
        R renderState = renderPassInfo.renderState();

        if (renderState.getOrDefaultGeckolibData(GraveyardDataTickets.LICH_PHASE, 0) != 5) {
            return;
        }

        float headYaw = renderState.getOrDefaultGeckolibData(DataTickets.ENTITY_YAW, 0.0F);
        snapshots.ifPresent("head", head -> head.setRotY(head.getRotY() - Mth.clamp(headYaw, -MAX_HEAD_YAW, MAX_HEAD_YAW) * Mth.DEG_TO_RAD));
    }

    @Override
    public RenderType getRenderType(R renderState, Identifier texture) {
        return RenderTypes.entityCutoutNoCull(texture);
    }

}
