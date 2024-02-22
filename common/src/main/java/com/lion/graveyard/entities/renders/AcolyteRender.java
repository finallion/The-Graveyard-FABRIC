package com.lion.graveyard.entities.renders;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.AcolyteEntity;
import com.lion.graveyard.entities.models.AcolyteModel;
import com.lion.graveyard.entities.renders.features.AcolyteEyes;
import com.lion.graveyard.init.TGEntityModelLayers;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Vindicator;

public class AcolyteRender extends MobRenderer<AcolyteEntity, AcolyteModel<AcolyteEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Graveyard.MOD_ID,"textures/entity/acolyte.png");

    public AcolyteRender(EntityRendererProvider.Context ctx) {
        super(ctx, new AcolyteModel<>(ctx.bakeLayer(TGEntityModelLayers.ACOLYTE_MODEL_LAYER)), 0.5F);
        this.addLayer(new AcolyteEyes(this));
        this.addLayer(new ItemInHandLayer<>(this, ctx.getItemInHandRenderer()) {

            public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, AcolyteEntity acolyte, float f, float g, float h, float j, float k, float l) {
                if (acolyte.isAggressive()) {
                    super.render(poseStack, multiBufferSource, i, acolyte, f, g, h, j, k, l);
                }

            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(AcolyteEntity entity) {
        return TEXTURE;
    }


}
