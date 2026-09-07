package com.lion.graveyard.entities.renders;

import com.lion.graveyard.entities.CorvidEntity;
import com.lion.graveyard.entities.models.CorvidModel;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CorvidRenderer extends GeoEntityRenderer<CorvidEntity> {
    /// The magpie is the same rig, drawn smaller.
    private static final float MAGPIE_SCALE = 0.8F;

    public CorvidRenderer(EntityRendererFactory.Context context) {
        super(context, new CorvidModel());
        this.shadowRadius = 0.2F;
    }

    @Override
    public void preRender(MatrixStack poseStack, CorvidEntity animatable, BakedGeoModel model, VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (animatable.isMagpie()) {
            poseStack.scale(MAGPIE_SCALE, MAGPIE_SCALE, MAGPIE_SCALE);
        }
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public RenderLayer getRenderType(CorvidEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return RenderLayer.getEntityCutoutNoCull(texture);
    }
}
