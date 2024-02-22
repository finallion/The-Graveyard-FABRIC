package com.lion.graveyard.entities.renders;

import com.lion.graveyard.entities.SkeletonCreeper;
import com.lion.graveyard.entities.renders.features.SkeletonCreeperChargeFeatureRenderer;
import com.lion.graveyard.entities.renders.features.SkeletonCreeperEyes;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Creeper;;

@Environment(EnvType.CLIENT)
public class SkeletonCreeperRender extends MobRenderer<SkeletonCreeper, CreeperModel<SkeletonCreeper>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("graveyard:textures/entity/skeleton_creeper.png");

    public SkeletonCreeperRender(EntityRendererProvider.Context context) {
        super(context, new CreeperModel<>(context.bakeLayer(ModelLayers.CREEPER)), 0.5F);
        this.addLayer(new SkeletonCreeperChargeFeatureRenderer<>(this, context.getModelSet()));
        this.addLayer(new SkeletonCreeperEyes(this));
    }

    protected void scale(Creeper creeperEntity, PoseStack matrixStack, float f) {
        float g = creeperEntity.getSwelling(f);
        float h = 1.0F + Mth.sin(g * 100.0F) * g * 0.01F;
        g = Mth.clamp(g, 0.0F, 1.0F);
        g *= g;
        g *= g;
        float i = (1.0F + g * 0.4F) * h;
        float j = (1.0F + g * 0.1F) / h;
        matrixStack.scale(i, j, i);
    }

    protected float getWhiteOverlayProgress(SkeletonCreeper creeperEntity, float f) {
        float g = creeperEntity.getSwelling(f);
        return (int)(g * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(g, 0.5F, 1.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(SkeletonCreeper creeperEntity) {
        return TEXTURE;
    }
}
