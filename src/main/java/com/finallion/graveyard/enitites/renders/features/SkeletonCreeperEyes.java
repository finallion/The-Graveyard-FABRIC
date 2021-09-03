package com.finallion.graveyard.enitites.renders.features;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SkeletonCreeperEyes extends FeatureRenderer<CreeperEntity, CreeperEntityModel<CreeperEntity>> {


    public SkeletonCreeperEyes(FeatureRendererContext<CreeperEntity, CreeperEntityModel<CreeperEntity>> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CreeperEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        VertexConsumer vertex = vertexConsumers.getBuffer(RenderLayer.getEyes(new Identifier("graveyard:textures/entity/skeleton_creeper_eyes.png")));

        this.getContextModel().render(matrices, vertex, 15728640, OverlayTexture.DEFAULT_UV, 1F, 1F, 1F, 1F);
    }



}

