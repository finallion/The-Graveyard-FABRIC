package com.lion.graveyard.entities.renders;

import com.lion.graveyard.entities.CorruptedIllager;
import com.lion.graveyard.entities.models.CorruptedIllagerModel;
import com.lion.graveyard.entities.renders.features.IllagerArmorFeatureRenderer;
import com.lion.graveyard.init.TGEntityModelLayers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.ArmorEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public abstract class CorruptedIllagerRenderer<T extends CorruptedIllager> extends MobEntityRenderer<T, CorruptedIllagerModel<T>> {
    protected CorruptedIllagerRenderer(EntityRendererFactory.Context ctx, CorruptedIllagerModel<T> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
        this.addFeature(new HeldItemFeatureRenderer<>(this, ctx.getHeldItemRenderer()));
        this.addFeature(new HeadFeatureRenderer<>(this, ctx.getModelLoader(), ctx.getHeldItemRenderer()));
        this.addFeature(new ArmorFeatureRenderer<>(this, new IllagerArmorFeatureRenderer<>(ctx.getPart(TGEntityModelLayers.CORRUPTED_ILLAGER_INNER_ARMOR_MODEL_LAYER)), new IllagerArmorFeatureRenderer<>(ctx.getPart(TGEntityModelLayers.CORRUPTED_ILLAGER_OUTER_ARMOR_MODEL_LAYER)), ctx.getModelManager()));
    }

    protected void scale(T illagerEntity, MatrixStack matrixStack, float f) {
        matrixStack.scale(0.9375F, 0.9375F, 0.9375F);
    }
}

