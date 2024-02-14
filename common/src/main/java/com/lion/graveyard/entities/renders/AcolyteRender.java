package com.lion.graveyard.entities.renders;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.AcolyteEntity;
import com.lion.graveyard.entities.CorruptedIllager;
import com.lion.graveyard.entities.models.AcolyteModel;
import com.lion.graveyard.entities.models.CorruptedIllagerModel;
import com.lion.graveyard.entities.renders.features.AcolyteEyes;
import com.lion.graveyard.init.TGEntityModelLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class AcolyteRender extends MobEntityRenderer<AcolyteEntity, AcolyteModel<AcolyteEntity>> {
    private static final Identifier TEXTURE = new Identifier(Graveyard.MOD_ID,"textures/entity/acolyte.png");

    public AcolyteRender(EntityRendererFactory.Context ctx) {
        super(ctx, new AcolyteModel<>(ctx.getPart(TGEntityModelLayers.ACOLYTE_MODEL_LAYER)), 0.5F);
        this.addFeature(new AcolyteEyes(this));
        this.addFeature(new HeldItemFeatureRenderer<>(this, ctx.getHeldItemRenderer()) {
            public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, AcolyteEntity acolyte, float f, float g, float h, float j, float k, float l) {
                if (acolyte.isAttacking()) {
                    super.render(matrixStack, vertexConsumerProvider, i, acolyte, f, g, h, j, k, l);
                }

            }
        });
    }

    @Override
    public Identifier getTexture(AcolyteEntity entity) {
        return TEXTURE;
    }


}
