package com.lion.graveyard.entities.renders.features;

import com.lion.graveyard.entities.CorvidEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.RenderUtils;

/// `BlockAndItemGeoLayer` applies the bone rotation twice, so this layer does the bone transform itself.
public class CorvidHeldItemLayer extends GeoRenderLayer<CorvidEntity> {

    public CorvidHeldItemLayer(GeoRenderer<CorvidEntity> renderer) {
        super(renderer);
    }

    @Override
    public void renderForBone(MatrixStack poseStack, CorvidEntity animatable, GeoBone bone, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        ItemStack stack = animatable.getEquippedStack(EquipmentSlot.MAINHAND);

        if (!bone.getName().equals("head") || stack.isEmpty()) {
            return;
        }

        poseStack.push();
        RenderUtils.translateToPivotPoint(poseStack, bone);
        // Beak tip: head pivot at z=-6, beak end at z=-9, two units above the pivot.
        poseStack.translate(0.0F, 0.09F, -0.24F);
        poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
        poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
        poseStack.scale(0.6F, 0.6F, 0.6F);
        MinecraftClient.getInstance().getItemRenderer().renderItem(animatable, stack, ModelTransformationMode.GROUND, false,
                poseStack, bufferSource, animatable.getWorld(), packedLight, packedOverlay, animatable.getId());
        poseStack.pop();

        // GeckoLib needs the bone's buffer again after an item render.
        bufferSource.getBuffer(renderType);
    }
}
