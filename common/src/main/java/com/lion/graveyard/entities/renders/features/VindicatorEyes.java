package com.lion.graveyard.entities.renders.features;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.CorruptedVindicator;
import com.lion.graveyard.entities.models.CorruptedIllagerModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class VindicatorEyes extends FeatureRenderer<CorruptedVindicator, CorruptedIllagerModel<CorruptedVindicator>> {

    public VindicatorEyes(FeatureRendererContext<CorruptedVindicator, CorruptedIllagerModel<CorruptedVindicator>> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CorruptedVindicator entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        VertexConsumer vertex = vertexConsumers.getBuffer(RenderLayer.getEyes(new Identifier(Graveyard.MOD_ID,"textures/entity/corrupted_illager_eyes.png")));

        this.getContextModel().render(matrices, vertex, 15728640, OverlayTexture.DEFAULT_UV, 1F, 1F, 1F, 1F);
    }



}

