package com.finallion.graveyard.entities.renders;

import com.finallion.graveyard.entities.Acolyte;
import com.finallion.graveyard.entities.renders.features.AcolyteEyes;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.IllagerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.util.Identifier;

public class AcolyteRender extends IllagerEntityRenderer<VindicatorEntity> {
    private static final Identifier TEXTURE = new Identifier("graveyard:textures/entity/acolyte.png");

    public AcolyteRender(EntityRendererFactory.Context ctx) {
        super(ctx, new IllagerEntityModel<>(ctx.getPart(EntityModelLayers.VINDICATOR)), 0.5F);
        this.addFeature(new AcolyteEyes(this));
        this.model.getHat().visible = true;
        this.addFeature(new HeldItemFeatureRenderer(this) {
            public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, Acolyte acolyte, float f, float g, float h, float j, float k, float l) {
                if (acolyte.isAttacking()) {
                    super.render(matrixStack, vertexConsumerProvider, i, acolyte, f, g, h, j, k, l);
                }

            }
        });
    }

    @Override
    public Identifier getTexture(VindicatorEntity entity) {
        return TEXTURE;
    }
}
