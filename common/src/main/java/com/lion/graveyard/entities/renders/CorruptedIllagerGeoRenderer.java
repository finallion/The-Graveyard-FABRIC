package com.lion.graveyard.entities.renders;

import com.lion.graveyard.client.GraveyardDataTickets;
import com.lion.graveyard.entities.AnimatedCorruptedIllager;
import com.lion.graveyard.entities.models.CorruptedIllagerGeoModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class CorruptedIllagerGeoRenderer<T extends AnimatedCorruptedIllager, R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<T, R> {

    public CorruptedIllagerGeoRenderer(EntityRendererProvider.Context context) {
        super(context, new CorruptedIllagerGeoModel<>());
        this.shadowRadius = 0.5F;
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    protected float getDeathMaxRotation(GeoRenderState renderState) {
        return 0.0F;
    }

    @Override
    public void addRenderData(T animatable, Void relatedObject, R renderState) {
        renderState.addGeckolibData(GraveyardDataTickets.ILLAGER_LEFT_CLAW, animatable.hasLeftClaw());
        renderState.addGeckolibData(GraveyardDataTickets.ILLAGER_RIGHT_CLAW, animatable.hasRightClaw());
    }

    @Override
    public void renderRecursively(
            R renderState,
            PoseStack poseStack,
            GeoBone bone,
            RenderType renderType,
            MultiBufferSource bufferSource,
            VertexConsumer buffer,
            boolean isReRender,
            int packedLight,
            int packedOverlay,
            int renderColor
    ) {
        // Illagers share one baked model. Set claw visibility for each entity.
        if (bone.getName().equals("left_claw")) {
            bone.setHidden(!renderState.getGeckolibData(GraveyardDataTickets.ILLAGER_LEFT_CLAW));
        } else if (bone.getName().equals("right_claw")) {
            bone.setHidden(!renderState.getGeckolibData(GraveyardDataTickets.ILLAGER_RIGHT_CLAW));
        }

        super.renderRecursively(renderState, poseStack, bone, renderType, bufferSource, buffer, isReRender, packedLight, packedOverlay, renderColor);
    }
}
