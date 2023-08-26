package main.java.com.lion.graveyard.entities.renders;

import main.java.com.lion.graveyard.entities.projectiles.SkullEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SkullEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SkullEntityRenderer extends EntityRenderer<SkullEntity> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/skeleton/skeleton.png");
    private final SkullEntityModel model;

    public SkullEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new SkullEntityModel(context.getPart(EntityModelLayers.SKELETON_SKULL));
    }

    protected int getBlockLight(SkullEntity witherSkullEntity, BlockPos blockPos) {
        return 15;
    }

    public void render(SkullEntity skullEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.scale(-1.0F, -1.0F, 1.0F);
        float h = MathHelper.lerpAngleDegrees(g, skullEntity.prevYaw, skullEntity.getYaw());
        float j = MathHelper.lerp(g, skullEntity.prevPitch, skullEntity.getPitch());
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(this.getTexture(skullEntity)));
        this.model.setHeadRotation(0.0F, h, j);
        this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStack.pop();
        super.render(skullEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public Identifier getTexture(SkullEntity entity) {
        return TEXTURE;
    }

}

