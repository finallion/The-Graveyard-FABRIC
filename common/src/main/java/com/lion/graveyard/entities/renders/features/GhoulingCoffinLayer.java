package com.lion.graveyard.entities.renders.features;

import com.lion.graveyard.client.GraveyardDataTickets;
import com.lion.graveyard.entities.GhoulingEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.function.BiConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.base.PerBoneRender;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

/// The renderer hides the torso bone in the main pass.
public class GhoulingCoffinLayer<R extends GeoRenderState> extends GeoRenderLayer<GhoulingEntity, Void, R> {

    public GhoulingCoffinLayer(GeoRenderer<GhoulingEntity, Void, R> renderer) {
        super(renderer);
    }

    @Override
    public void addPerBoneRender(R renderState, BakedGeoModel model, BiConsumer<GeoBone, PerBoneRender<R>> consumer) {
        if (!renderState.getOrDefaultGeckolibData(GraveyardDataTickets.GHOULING_HAS_COFFIN, false)) {
            return;
        }

        if (isMainPassSkipped(renderState)) {
            return;
        }

        model.getBone("torso").ifPresent(torso -> consumer.accept(torso, this::renderTorso));
    }

    private boolean isMainPassSkipped(R renderState) {
        return this.renderer.getRenderType(renderState, this.renderer.getTextureLocation(renderState)) == null;
    }

    private void renderTorso(
            R renderState,
            PoseStack poseStack,
            GeoBone torso,
            @Nullable RenderType renderType,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay,
            int renderColor
    ) {
        if (renderType == null) {
            return;
        }

        renderOffhandItem(renderState, poseStack, bufferSource, packedLight, packedOverlay);

        RenderType translucent = RenderType.entityTranslucent(this.renderer.getTextureLocation(renderState));
        VertexConsumer buffer = bufferSource.getBuffer(translucent);

        for (GeoCube cube : torso.getCubes()) {
            poseStack.pushPose();
            this.renderer.renderCube(renderState, cube, poseStack, buffer, packedLight, packedOverlay, renderColor);
            poseStack.popPose();
        }

        for (GeoBone child : torso.getChildBones()) {
            this.renderer.renderRecursively(renderState, poseStack, child, translucent, bufferSource, buffer, true, packedLight, packedOverlay, renderColor);
        }
    }

    private void renderOffhandItem(
            R renderState,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay
    ) {
        ItemStack stack = renderState.getGeckolibData(GraveyardDataTickets.GHOULING_OFFHAND);

        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(90));
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.mulPose(Axis.ZP.rotationDegrees(-25));
        poseStack.translate(1.3D, 0.86D, 0.0D);
        poseStack.scale(2.0F, 2.0F, 2.0F);
        Minecraft.getInstance().getItemRenderer().renderStatic(
                stack,
                ItemDisplayContext.THIRD_PERSON_LEFT_HAND,
                packedLight,
                packedOverlay,
                poseStack,
                bufferSource,
                Minecraft.getInstance().level,
                0
        );
        poseStack.popPose();
    }
}
