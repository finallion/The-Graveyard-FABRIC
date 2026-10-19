package com.lion.graveyard.entities.renders.features;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.models.CorruptedIllagerModel;
import com.lion.graveyard.entities.renders.CorruptedIllagerRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;

@Environment(EnvType.CLIENT)
public class AcolyteEyes extends RenderLayer<CorruptedIllagerRenderState, CorruptedIllagerModel> {


    public AcolyteEyes(RenderLayerParent<CorruptedIllagerRenderState, CorruptedIllagerModel> context) {
        super(context);
    }

    @Override
    public void render(
            PoseStack matrices,
            MultiBufferSource vertexConsumers,
            int light,
            CorruptedIllagerRenderState renderState,
            float yRot,
            float xRot
    ) {
        VertexConsumer vertex = vertexConsumers.getBuffer(RenderTypes.eyes(Graveyard.id("textures/entity/acolyte_eyes.png")));

        this.getParentModel().renderToBuffer(matrices, vertex, 15728640, OverlayTexture.NO_OVERLAY, -1);
    }



}

