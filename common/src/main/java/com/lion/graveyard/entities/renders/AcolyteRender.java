package com.lion.graveyard.entities.renders;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.AcolyteEntity;
import com.lion.graveyard.entities.models.CorruptedIllagerModel;
import com.lion.graveyard.entities.renders.features.AcolyteEyes;
import com.lion.graveyard.init.TGEntityModelLayers;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public class AcolyteRender extends CorruptedIllagerRenderer<AcolyteEntity> {
    private static final ResourceLocation TEXTURE = Graveyard.id("textures/entity/acolyte.png");

    public AcolyteRender(EntityRendererProvider.Context ctx) {
        super(ctx, new CorruptedIllagerModel(ctx.bakeLayer(TGEntityModelLayers.CORRUPTED_ILLAGER_MODEL_LAYER)), 0.5F);
        this.addLayer(new AcolyteEyes(this));
        this.model.getHat().visible = true;
        this.addLayer(new ItemInHandLayer<>(this) {
            @Override
            public void render(
                    PoseStack matrixStack,
                    MultiBufferSource vertexConsumerProvider,
                    int light,
                    CorruptedIllagerRenderState renderState,
                    float yRot,
                    float xRot
            ) {
                if (renderState.chasing) {
                    super.render(matrixStack, vertexConsumerProvider, light, renderState, yRot, xRot);
                }

            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(CorruptedIllagerRenderState renderState) {
        return TEXTURE;
    }


}
