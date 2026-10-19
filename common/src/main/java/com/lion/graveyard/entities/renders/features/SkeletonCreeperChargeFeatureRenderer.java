package com.lion.graveyard.entities.renders.features;

import net.minecraft.client.model.monster.creeper.CreeperModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EnergySwirlLayer;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.resources.Identifier;

public class SkeletonCreeperChargeFeatureRenderer extends EnergySwirlLayer<CreeperRenderState, CreeperModel> {
    private static final Identifier SKIN = Identifier.withDefaultNamespace("textures/entity/creeper/creeper_armor.png");
    private final CreeperModel model;

    public SkeletonCreeperChargeFeatureRenderer(
            RenderLayerParent<CreeperRenderState, CreeperModel> context,
            EntityModelSet loader
    ) {
        super(context);
        this.model = new CreeperModel(loader.bakeLayer(ModelLayers.CREEPER_ARMOR));
    }

    @Override
    protected boolean isPowered(CreeperRenderState renderState) {
        return renderState.isPowered;
    }

    @Override
    protected float xOffset(float partialAge) {
        return partialAge * 0.01F;
    }

    @Override
    protected Identifier getTextureLocation() {
        return SKIN;
    }

    @Override
    protected CreeperModel model() {
        return this.model;
    }
}
