package com.lion.graveyard.entities.renders.features;

import com.lion.graveyard.entities.CorvidEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.RenderUtil;

/// `BlockAndItemGeoLayer` applies the bone rotation twice, so this layer does the bone transform itself.
public class CorvidHeldItemLayer extends GeoRenderLayer<CorvidEntity> {

    public CorvidHeldItemLayer(GeoRenderer<CorvidEntity> renderer) {
        super(renderer);
    }

    @Override
    public void renderForBone(PoseStack poseStack, CorvidEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        ItemStack stack = animatable.getItemBySlot(EquipmentSlot.MAINHAND);

        if (!bone.getName().equals("head") || stack.isEmpty()) {
            return;
        }

        poseStack.pushPose();
        RenderUtil.translateToPivotPoint(poseStack, bone);
        // Beak tip: head pivot at z=-6, beak end at z=-9, two units above the pivot.
        poseStack.translate(0.0F, 0.09F, -0.24F);
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        poseStack.scale(0.6F, 0.6F, 0.6F);
        Minecraft.getInstance().getItemRenderer().renderStatic(animatable, stack, ItemDisplayContext.GROUND, false,
                poseStack, bufferSource, animatable.level(), packedLight, packedOverlay, animatable.getId());
        poseStack.popPose();

        // GeckoLib needs the bone's buffer again after an item render.
        bufferSource.getBuffer(renderType);
    }
}
