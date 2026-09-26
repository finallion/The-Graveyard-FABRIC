package com.lion.graveyard.entities.renders;

import com.lion.graveyard.entities.CorvidEntity;
import com.lion.graveyard.entities.models.CorvidModel;
import com.lion.graveyard.entities.renders.features.CorvidHeldItemLayer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CorvidRenderer extends GeoEntityRenderer<CorvidEntity> {

    public CorvidRenderer(EntityRendererFactory.Context context) {
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
    public RenderLayer getRenderType(CorvidEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return RenderLayer.getEntityCutoutNoCull(texture);
    }
}
