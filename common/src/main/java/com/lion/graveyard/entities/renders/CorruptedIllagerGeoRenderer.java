package com.lion.graveyard.entities.renders;

import com.lion.graveyard.entities.AnimatedCorruptedIllager;
import com.lion.graveyard.entities.models.CorruptedIllagerGeoModel;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class CorruptedIllagerGeoRenderer<T extends AnimatedCorruptedIllager> extends GeoEntityRenderer<T> {

    public CorruptedIllagerGeoRenderer(EntityRendererFactory.Context context) {
        super(context, new CorruptedIllagerGeoModel<>());
        this.shadowRadius = 0.5F;
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    protected float getDeathMaxRotation(T entity) {
        return 0.0F;
    }

    @Override
    public void renderRecursively(MatrixStack poseStack, T animatable, GeoBone bone, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        // Illagers share one baked model. Set claw visibility for each entity.
        if (bone.getName().equals("left_claw")) {
            bone.setHidden(!animatable.hasLeftClaw());
        } else if (bone.getName().equals("right_claw")) {
            bone.setHidden(!animatable.hasRightClaw());
        }
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
