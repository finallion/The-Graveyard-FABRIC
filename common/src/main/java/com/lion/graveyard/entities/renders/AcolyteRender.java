package com.lion.graveyard.entities.renders;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.AcolyteEntity;
import com.lion.graveyard.entities.models.CorruptedIllagerModel;
import com.lion.graveyard.entities.renders.features.AcolyteEyes;
import com.lion.graveyard.init.TGEntityModelLayers;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.Identifier;

public class AcolyteRender extends CorruptedIllagerRenderer<AcolyteEntity> {
    private static final Identifier TEXTURE = Graveyard.id("textures/entity/acolyte.png");

    public AcolyteRender(EntityRendererProvider.Context ctx) {
        super(ctx, new CorruptedIllagerModel(ctx.bakeLayer(TGEntityModelLayers.CORRUPTED_ILLAGER_MODEL_LAYER)), 0.5F);
        this.addLayer(new AcolyteEyes(this));
        this.model.getHat().visible = true;
        this.addLayer(new ItemInHandLayer<>(this) {
            @Override
            public void submit(
                    PoseStack matrixStack,
                    SubmitNodeCollector collector,
                    int light,
                    CorruptedIllagerRenderState renderState,
                    float yRot,
                    float xRot
            ) {
                if (renderState.chasing) {
                    super.submit(matrixStack, collector, light, renderState, yRot, xRot);
                }

            }
        });
    }

    @Override
    public Identifier getTextureLocation(CorruptedIllagerRenderState renderState) {
        return TEXTURE;
    }


}
