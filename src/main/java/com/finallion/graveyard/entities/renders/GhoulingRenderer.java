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
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class GhoulingRenderer extends GeoEntityRenderer<GhoulingEntity> {
    private GhoulingEntity ghouling;

    public GhoulingRenderer(EntityRendererFactory.Context context) {
        super(context, new GhoulingModel());
        this.shadowRadius = 0.6F;
    }

    @Override
    public RenderLayer getRenderType(GhoulingEntity animatable, float partialTicks, MatrixStack stack, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, Identifier textureLocation) {
        ghouling = animatable;
        return RenderLayer.getEntityCutoutNoCull(textureLocation);
    }

    // stops the vanilla death animation
    @Override
    protected float getDeathMaxRotation(GhoulingEntity entityLivingBaseIn) {
        return 0.0F;
    }

    @Override
    public void renderRecursively(GeoBone bone, MatrixStack stack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (bone.getName().equals("chain") && ghouling != null) {
            bone.setHidden(!ghouling.hasCoffin());
        }

        if (bone.getName().equals("torso") && ghouling != null && ghouling.hasCoffin()) {
            stack.push();
            stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90));
            stack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90));
            stack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-25));
            stack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(bone.getRotationZ()));
            stack.translate(1.3D, 0.86D, 0.0D);
            stack.scale(2.0F, 2.0F, 2.0F);
            MinecraftClient.getInstance().getItemRenderer().renderItem(offHand, ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND, packedLightIn, packedOverlayIn, stack, this.rtb, 0);
            stack.pop();
            bufferIn = rtb.getBuffer(RenderLayer.getEntityTranslucent(whTexture));
        }
        super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

}
