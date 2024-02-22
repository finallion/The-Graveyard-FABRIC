package com.lion.graveyard.entities.renders;

import com.lion.graveyard.entities.CorruptedIllager;
import com.lion.graveyard.entities.models.CorruptedIllagerModel;
import com.lion.graveyard.entities.renders.features.IllagerArmorFeatureRenderer;
import com.lion.graveyard.init.TGEntityModelLayers;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;

@Environment(EnvType.CLIENT)
public abstract class CorruptedIllagerRenderer<T extends CorruptedIllager> extends MobRenderer<T, CorruptedIllagerModel<T>> {
    protected CorruptedIllagerRenderer(EntityRendererProvider.Context ctx, CorruptedIllagerModel<T> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
        this.addLayer(new ItemInHandLayer<>(this, ctx.getItemInHandRenderer()));
        this.addLayer(new CustomHeadLayer<>(this, ctx.getModelSet(), ctx.getItemInHandRenderer()));
        this.addLayer(new HumanoidArmorLayer<>(this, new IllagerArmorFeatureRenderer<>(ctx.bakeLayer(TGEntityModelLayers.CORRUPTED_ILLAGER_INNER_ARMOR_MODEL_LAYER)), new IllagerArmorFeatureRenderer<>(ctx.bakeLayer(TGEntityModelLayers.CORRUPTED_ILLAGER_OUTER_ARMOR_MODEL_LAYER)), ctx.getModelManager()));
    }

    protected void scale(T illagerEntity, PoseStack matrixStack, float f) {
        matrixStack.scale(0.9375F, 0.9375F, 0.9375F);
    }
}

