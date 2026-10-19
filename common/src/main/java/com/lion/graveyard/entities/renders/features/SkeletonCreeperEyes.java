package com.lion.graveyard.entities.renders.features;

import com.lion.graveyard.Graveyard;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.monster.creeper.CreeperModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;

@Environment(EnvType.CLIENT)
public class SkeletonCreeperEyes extends EyesLayer<CreeperRenderState, CreeperModel> {
    private static final RenderType EYES = RenderTypes.eyes(Graveyard.id("textures/entity/skeleton_creeper_eyes.png"));

    public SkeletonCreeperEyes(RenderLayerParent<CreeperRenderState, CreeperModel> context) {
        super(context);
    }

    @Override
    public RenderType renderType() {
        return EYES;
    }
}
