package com.lion.graveyard.entities.renders;

import com.lion.graveyard.entities.TormentedVillagerEntity;
import com.lion.graveyard.entities.models.TormentedVillagerModel;
import com.lion.graveyard.entities.renders.features.TormentedVillagerEyes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TormentedVillagerRenderer extends GeoEntityRenderer<TormentedVillagerEntity> {

    public TormentedVillagerRenderer(EntityRendererProvider.Context context) {
        super(context, new TormentedVillagerModel());
        this.addRenderLayer(new TormentedVillagerEyes(this));
        this.shadowRadius = 0.4F;
    }

    @Override
    protected float getDeathMaxRotation(TormentedVillagerEntity entity) {
        return 0.0F;
    }

    @Override
    public RenderType getRenderType(TormentedVillagerEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }
}
