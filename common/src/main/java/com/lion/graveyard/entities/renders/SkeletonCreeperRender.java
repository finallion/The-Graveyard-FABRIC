package com.lion.graveyard.entities.renders;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.SkeletonCreeper;
import com.lion.graveyard.entities.renders.features.SkeletonCreeperChargeFeatureRenderer;
import com.lion.graveyard.entities.renders.features.SkeletonCreeperEyes;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.monster.creeper.CreeperModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

@Environment(EnvType.CLIENT)
public class SkeletonCreeperRender extends MobRenderer<SkeletonCreeper, CreeperRenderState, CreeperModel> {

    private static final Identifier TEXTURE = Graveyard.id("textures/entity/skeleton_creeper.png");

    public SkeletonCreeperRender(EntityRendererProvider.Context context) {
        super(context, new CreeperModel(context.bakeLayer(ModelLayers.CREEPER)), 0.5F);
        this.addLayer(new SkeletonCreeperChargeFeatureRenderer(this, context.getModelSet()));
        this.addLayer(new SkeletonCreeperEyes(this));
    }

    @Override
    public CreeperRenderState createRenderState() {
        return new CreeperRenderState();
    }

    @Override
    public void extractRenderState(SkeletonCreeper creeper, CreeperRenderState renderState, float partialTick) {
        super.extractRenderState(creeper, renderState, partialTick);
        renderState.swelling = creeper.getSwelling(partialTick);
        renderState.isPowered = creeper.isPowered();
    }

    @Override
    protected void scale(CreeperRenderState renderState, PoseStack matrixStack) {
        float g = renderState.swelling;
        float h = 1.0F + Mth.sin(g * 100.0F) * g * 0.01F;
        g = Mth.clamp(g, 0.0F, 1.0F);
        g *= g;
        g *= g;
        float i = (1.0F + g * 0.4F) * h;
        float j = (1.0F + g * 0.1F) / h;
        matrixStack.scale(i, j, i);
    }

    @Override
    protected float getWhiteOverlayProgress(CreeperRenderState renderState) {
        float g = renderState.swelling;
        return (int)(g * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(g, 0.5F, 1.0F);
    }

    @Override
    public Identifier getTextureLocation(CreeperRenderState renderState) {
        return TEXTURE;
    }
}
