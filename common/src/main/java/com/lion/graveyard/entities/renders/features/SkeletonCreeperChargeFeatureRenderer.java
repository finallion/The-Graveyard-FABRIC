package com.lion.graveyard.entities.renders.features;

import net.minecraft.client.render.entity.feature.EnergySwirlOverlayFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.util.Identifier;

public class SkeletonCreeperChargeFeatureRenderer<T extends CreeperEntity> extends EnergySwirlOverlayFeatureRenderer<T, CreeperEntityModel<T>> {
    private static final Identifier SKIN = new Identifier("textures/entity/creeper/creeper_armor.png");
    private final CreeperEntityModel<CreeperEntity> model;

    public SkeletonCreeperChargeFeatureRenderer(FeatureRendererContext<T, CreeperEntityModel<T>> context, EntityModelLoader loader) {
        super(context);
        this.model = new CreeperEntityModel<>(loader.getModelPart(EntityModelLayers.CREEPER_ARMOR));
    }

    protected float getEnergySwirlX(float partialAge) {
        return partialAge * 0.01F;
    }

    protected Identifier getEnergySwirlTexture() {
        return SKIN;
    }

    protected EntityModel<T> getEnergySwirlModel() {
        return (EntityModel<T>) this.model;
    }
}

