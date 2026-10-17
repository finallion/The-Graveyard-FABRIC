package com.lion.graveyard.entities.renders.features;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.AcolyteEntity;
import com.lion.graveyard.entities.models.CorruptedIllagerModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;

@Environment(EnvType.CLIENT)
public class AcolyteEyes extends RenderLayer<AcolyteEntity, CorruptedIllagerModel<AcolyteEntity>> {


    public AcolyteEyes(RenderLayerParent<AcolyteEntity, CorruptedIllagerModel<AcolyteEntity>> context) {
        super(context);
    }

    @Override
    public void render(PoseStack matrices, MultiBufferSource vertexConsumers, int light, AcolyteEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        VertexConsumer vertex = vertexConsumers.getBuffer(RenderType.eyes(Graveyard.id("textures/entity/acolyte_eyes.png")));

        this.getParentModel().renderToBuffer(matrices, vertex, 15728640, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);
    }



}

