package com.lion.graveyard.entities.renders;

import com.lion.graveyard.entities.FallingCorpse;
import com.lion.graveyard.entities.models.FallingCorpseModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class FallingCorpseRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<FallingCorpse, R> {

    public FallingCorpseRenderer(EntityRendererProvider.Context context) {
        super(context, new FallingCorpseModel());
        this.shadowRadius = 0.4F;
    }

    @Override
    public void addRenderData(FallingCorpse animatable, Void relatedObject, R renderState) {
        renderState.addGeckolibData(DataTickets.ENTITY_BODY_YAW, animatable.getRotation());
    }

    @Override
    public RenderType getRenderType(R renderState, ResourceLocation texture) {
        return RenderType.entityCutoutNoCull(texture);
    }

}
