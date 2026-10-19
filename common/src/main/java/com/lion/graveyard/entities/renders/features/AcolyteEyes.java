package com.lion.graveyard.entities.renders.features;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.models.CorruptedIllagerModel;
import com.lion.graveyard.entities.renders.CorruptedIllagerRenderState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;

@Environment(EnvType.CLIENT)
public class AcolyteEyes extends EyesLayer<CorruptedIllagerRenderState, CorruptedIllagerModel> {
    private static final RenderType EYES = RenderTypes.eyes(Graveyard.id("textures/entity/acolyte_eyes.png"));

    public AcolyteEyes(RenderLayerParent<CorruptedIllagerRenderState, CorruptedIllagerModel> context) {
        super(context);
    }

    @Override
    public RenderType renderType() {
        return EYES;
    }
}
