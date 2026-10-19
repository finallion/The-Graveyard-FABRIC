package com.lion.graveyard.entities.renders;

import com.lion.graveyard.client.GraveyardDataTickets;
import com.lion.graveyard.entities.GhoulingEntity;
import com.lion.graveyard.entities.animation.BoneReturn;
import com.lion.graveyard.entities.models.GhoulingModel;
import com.lion.graveyard.entities.renders.features.GhoulingCoffinLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.BoneSnapshots;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.RenderPassInfo;

public class GhoulingRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<GhoulingEntity, R> {

    public GhoulingRenderer(EntityRendererProvider.Context context) {
        super(context, new GhoulingModel());
        this.shadowRadius = 0.6F;
        this.withRenderLayer(new GhoulingCoffinLayer<>(this));
    }

    @Override
    public void applyAnimationControllers(RenderPassInfo<R> renderPassInfo, BoneSnapshots boneSnapshots) {
        super.applyAnimationControllers(renderPassInfo, boneSnapshots);
        BoneReturn.apply(renderPassInfo, boneSnapshots);
    }

    // stops the vanilla death animation
    @Override
    protected float getDeathMaxRotation(GeoRenderState renderState) {
        return 0.0F;
    }

    @Override
    public void addRenderData(GhoulingEntity animatable, Void relatedObject, R renderState, float partialTick) {
        renderState.addGeckolibData(GraveyardDataTickets.GHOULING_HAS_COFFIN, animatable.hasCoffin());
        renderState.addGeckolibData(GraveyardDataTickets.GHOULING_OFFHAND, animatable.getItemBySlot(EquipmentSlot.OFFHAND).copy());
    }

    @Override
    public void adjustModelBonesForRender(RenderPassInfo<R> renderPassInfo, BoneSnapshots snapshots) {
        boolean hasCoffin = renderPassInfo.renderState().getGeckolibData(GraveyardDataTickets.GHOULING_HAS_COFFIN);

        snapshots.ifPresent("chain", chain -> chain.skipRender(!hasCoffin));
    }

}
