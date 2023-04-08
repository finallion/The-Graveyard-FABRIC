package com.finallion.graveyard.entities.renders;

import com.finallion.graveyard.entities.GhoulingEntity;
import com.finallion.graveyard.entities.LichEntity;
import com.finallion.graveyard.entities.models.GhoulingModel;
import com.finallion.graveyard.entities.models.LichModel;
import com.finallion.graveyard.entities.renders.features.LichEyesFeatureRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GhoulingRenderer extends GeoEntityRenderer<GhoulingEntity> {
    private GhoulingEntity ghouling;

    public GhoulingRenderer(EntityRendererFactory.Context context) {
        super(context, new GhoulingModel());
        this.shadowRadius = 0.6F;
    }

    @Override
    public RenderLayer getRenderType(GhoulingEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        ghouling = animatable;
        return RenderLayer.getEntityCutoutNoCull(texture);
    }

    // stops the vanilla death animation
    @Override
    protected float getDeathMaxRotation(GhoulingEntity entityLivingBaseIn) {
        return 0.0F;
    }

    @Override
    public void renderRecursively(MatrixStack poseStack, GhoulingEntity animatable, GeoBone bone, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (bone.getName().equals("chain") && ghouling != null) {
            bone.setHidden(!ghouling.hasCoffin());
        }

        if (bone.getName().equals("torso") && ghouling != null && ghouling.hasCoffin()) {
            poseStack.push();
            poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
            poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
            poseStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-25));
            //stack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(bone.getRotationZ()));
            poseStack.translate(1.3D, 0.86D, 0.0D);
            poseStack.scale(2.0F, 2.0F, 2.0F);
            MinecraftClient.getInstance().getItemRenderer().renderItem(ghouling.getEquippedStack(EquipmentSlot.OFFHAND), ModelTransformationMode.THIRD_PERSON_LEFT_HAND, packedLight, packedOverlay, poseStack, bufferSource, animatable.world, 0);
            poseStack.pop();
            buffer = bufferSource.getBuffer(RenderLayer.getEntityTranslucent(getTexture(animatable)));
        }
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }


}
