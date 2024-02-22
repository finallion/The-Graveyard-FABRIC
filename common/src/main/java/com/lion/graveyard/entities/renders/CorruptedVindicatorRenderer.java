package com.lion.graveyard.entities.renders;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.CorruptedVindicator;
import com.lion.graveyard.entities.models.CorruptedIllagerModel;
import com.lion.graveyard.entities.renders.features.VindicatorEyes;
import com.lion.graveyard.init.TGEntityModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class CorruptedVindicatorRenderer extends CorruptedIllagerRenderer<CorruptedVindicator> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Graveyard.MOD_ID,"textures/entity/corrupted_vindicator.png");

    public CorruptedVindicatorRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new CorruptedIllagerModel<>(ctx.bakeLayer(TGEntityModelLayers.CORRUPTED_ILLAGER_MODEL_LAYER)), 0.5F);
        this.addLayer(new VindicatorEyes(this));
    }

    @Override
    public ResourceLocation getTextureLocation(CorruptedVindicator entity) {
        return TEXTURE;
    }


}

