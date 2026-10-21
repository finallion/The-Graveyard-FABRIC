package com.lion.graveyard.entities.renders;

import com.lion.graveyard.entities.projectiles.SkullEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.skull.SkullModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.WitherSkullRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

@Environment(EnvType.CLIENT)
public class SkullEntityRenderer extends EntityRenderer<SkullEntity, WitherSkullRenderState> {
    private static final Identifier TEXTURE = Identifier.withDefaultNamespace("textures/entity/skeleton/skeleton.png");
    private final SkullModel model;

    public SkullEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new SkullModel(context.bakeLayer(ModelLayers.SKELETON_SKULL));
    }

    @Override
    protected int getBlockLightLevel(SkullEntity witherSkullEntity, BlockPos blockPos) {
        return 15;
    }

    @Override
    public WitherSkullRenderState createRenderState() {
        return new WitherSkullRenderState();
    }

    @Override
    public void extractRenderState(SkullEntity skullEntity, WitherSkullRenderState renderState, float partialTick) {
        super.extractRenderState(skullEntity, renderState, partialTick);
        renderState.modelState.animationPos = 0.0F;
        renderState.modelState.yRot = Mth.rotLerp(partialTick, skullEntity.yRotO, skullEntity.getYRot());
        renderState.modelState.xRot = Mth.lerp(partialTick, skullEntity.xRotO, skullEntity.getXRot());
    }

    @Override
    public void submit(
            WitherSkullRenderState renderState,
            PoseStack matrixStack,
            SubmitNodeCollector collector,
            CameraRenderState cameraState
    ) {
        matrixStack.pushPose();
        matrixStack.scale(-1.0F, -1.0F, 1.0F);
        collector.submitModel(
                this.model,
                renderState.modelState,
                matrixStack,
                this.model.renderType(TEXTURE),
                renderState.lightCoords,
                OverlayTexture.NO_OVERLAY,
                renderState.outlineColor,
                null
        );
        matrixStack.popPose();
        super.submit(renderState, matrixStack, collector, cameraState);
    }

}
