package com.lion.graveyard.entities.renders;

import com.lion.graveyard.entities.CorruptedIllager;
import com.lion.graveyard.entities.models.CorruptedIllagerModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;

@Environment(EnvType.CLIENT)
public abstract class CorruptedIllagerRenderer<T extends CorruptedIllager> extends MobRenderer<T, CorruptedIllagerModel<T>> {
    protected CorruptedIllagerRenderer(
            EntityRendererProvider.Context ctx,
            CorruptedIllagerModel<T> model,
            float shadowRadius
    ) {
        super(ctx, model, shadowRadius);
        this.addLayer(new CustomHeadLayer(this, ctx.getModelSet(), ctx.getItemInHandRenderer()));
    }

    @Override
    protected void scale(T illagerEntity, PoseStack matrixStack, float f) {
        matrixStack.scale(0.9375F, 0.9375F, 0.9375F);
    }
}

