package com.lion.graveyard.entities.renders;

import com.lion.graveyard.client.GraveyardDataTickets;
import com.lion.graveyard.entities.GhoulingEntity;
import com.lion.graveyard.entities.models.GhoulingModel;
import com.lion.graveyard.entities.renders.features.GhoulingCoffinLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class GhoulingRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<GhoulingEntity, R> {

    public GhoulingRenderer(EntityRendererProvider.Context context) {
        super(context, new GhoulingModel());
        this.shadowRadius = 0.6F;
        this.addRenderLayer(new GhoulingCoffinLayer<>(this));
    }

    // stops the vanilla death animation
    @Override
    protected float getDeathMaxRotation(GeoRenderState renderState) {
        return 0.0F;
    }

    @Override
    public void addRenderData(GhoulingEntity animatable, Void relatedObject, R renderState) {
        renderState.addGeckolibData(GraveyardDataTickets.GHOULING_HAS_COFFIN, animatable.hasCoffin());
        renderState.addGeckolibData(GraveyardDataTickets.GHOULING_OFFHAND, animatable.getItemBySlot(EquipmentSlot.OFFHAND).copy());
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
        boolean hasCoffin = renderState.getGeckolibData(GraveyardDataTickets.GHOULING_HAS_COFFIN);

        if (bone.getName().equals("chain")) {
            bone.setHidden(!hasCoffin);
        }

        // The coffin layer draws the torso and its children translucent.
        if (bone.getName().equals("torso") && !isReRender) {
            bone.setHidden(hasCoffin);
            bone.setChildrenHidden(hasCoffin);
        }

        super.renderRecursively(renderState, poseStack, bone, renderType, bufferSource, buffer, isReRender, packedLight, packedOverlay, renderColor);
    }

    @Override
    public RenderType getRenderType(R renderState, ResourceLocation texture) {
        return RenderType.entityCutoutNoCull(texture);
    }

}
