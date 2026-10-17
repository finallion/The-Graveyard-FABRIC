package com.lion.graveyard.entities.renders;

import com.lion.graveyard.entities.LichEntity;
import com.lion.graveyard.entities.models.LichModel;
import com.lion.graveyard.entities.renders.features.LichEyesFeatureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class LichRenderer extends GeoEntityRenderer<LichEntity> {

    public LichRenderer(EntityRendererProvider.Context context) {
        super(context, new LichModel());
        this.shadowRadius = 1.0F;
        this.addRenderLayer(new LichEyesFeatureRenderer(this));
    }

    @Override
    public RenderType getRenderType(LichEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutoutNoCull(texture);
    }

    // stops the vanilla death animation
    @Override
    protected float getDeathMaxRotation(LichEntity entityLivingBaseIn) {
        return 0.0F;
    }

}
