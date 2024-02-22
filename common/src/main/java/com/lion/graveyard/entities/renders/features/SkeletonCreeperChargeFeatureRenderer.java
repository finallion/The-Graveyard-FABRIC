package com.lion.graveyard.entities.renders.features;

import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EnergySwirlLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Creeper;

public class SkeletonCreeperChargeFeatureRenderer<T extends Creeper> extends EnergySwirlLayer<T, CreeperModel<T>> {
    private static final ResourceLocation SKIN = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
    private final CreeperModel<Creeper> model;

    public SkeletonCreeperChargeFeatureRenderer(RenderLayerParent<T, CreeperModel<T>> context, EntityModelSet loader) {
        super(context);
        this.model = new CreeperModel<>(loader.bakeLayer(ModelLayers.CREEPER_ARMOR));
    }

    protected float xOffset(float partialAge) {
        return partialAge * 0.01F;
    }

    protected ResourceLocation getTextureLocation() {
        return SKIN;
    }

    protected EntityModel<T> model() {
        return (EntityModel<T>) this.model;
    }
}

