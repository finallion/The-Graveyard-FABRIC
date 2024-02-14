package com.lion.graveyard.blockentities.renders;


import com.lion.graveyard.blockentities.BrazierBlockEntity;
import com.lion.graveyard.blockentities.models.BrazierModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class BrazierBlockEntityRenderer extends GeoBlockRenderer<BrazierBlockEntity> {

    public BrazierBlockEntityRenderer() {
        super(new BrazierModel());
    }
    @Override
    public int getViewDistance() {
        return 32;
    }

    @Override
    public RenderType getRenderType(BrazierBlockEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(getTextureLocation(animatable));
    }
}
