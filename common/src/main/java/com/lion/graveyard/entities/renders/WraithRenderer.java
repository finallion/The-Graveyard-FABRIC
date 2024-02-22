package com.lion.graveyard.entities.renders;

import com.lion.graveyard.entities.WraithEntity;
import com.lion.graveyard.entities.models.WraithModel;
import com.lion.graveyard.entities.renders.features.WraithEyesFeatureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WraithRenderer extends GeoEntityRenderer<WraithEntity> {

    public WraithRenderer(EntityRendererProvider.Context context) {
        super(context, new WraithModel());
        this.addRenderLayer(new WraithEyesFeatureRenderer(this));
        this.shadowRadius = 0.3F;
    }

    // stops the vanilla death animation
    @Override
    protected float getDeathMaxRotation(WraithEntity entityLivingBaseIn) {
        return 0.0F;
    }

    @Override
    public RenderType getRenderType(WraithEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }

}
