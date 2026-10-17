package com.lion.graveyard.entities.renders;

import com.lion.graveyard.entities.CorvidEntity;
import com.lion.graveyard.entities.models.CorvidModel;
import com.lion.graveyard.entities.renders.features.CorvidHeldItemLayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CorvidRenderer extends GeoEntityRenderer<CorvidEntity> {

    public CorvidRenderer(EntityRendererProvider.Context context) {
        super(context, new CorvidModel());
        this.shadowRadius = 0.2F;
        this.addRenderLayer(new CorvidHeldItemLayer(this));
    }

    @Override
    protected float getDeathMaxRotation(CorvidEntity animatable) {
        // The death animation lays the bird down.
        return 0.0F;
    }

    @Override
    public RenderType getRenderType(CorvidEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutoutNoCull(texture);
    }
}
