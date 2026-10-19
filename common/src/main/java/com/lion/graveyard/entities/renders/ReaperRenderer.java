package com.lion.graveyard.entities.renders;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.ReaperEntity;
import com.lion.graveyard.entities.animation.BoneReturn;
import com.lion.graveyard.entities.models.ReaperModel;
import com.lion.graveyard.entities.renders.features.GraveyardEyesLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.BoneSnapshots;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.RenderPassInfo;

public class ReaperRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<ReaperEntity, R> {
    private static final Identifier EYES = Graveyard.id("textures/entity/reaper_eyes.png");

    public ReaperRenderer(EntityRendererProvider.Context context) {
        super(context, new ReaperModel());
        this.withRenderLayer(new GraveyardEyesLayer<>(this, EYES));
        this.shadowRadius = 0.4F;
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

}
