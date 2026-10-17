package com.lion.graveyard.blockentities.renders;

import com.lion.graveyard.blockentities.OssuaryBlockEntity;
import com.lion.graveyard.blockentities.models.OssuaryModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.texture.AnimatableTexture;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class OssuaryBlockEntityRenderer extends GeoBlockRenderer<OssuaryBlockEntity> {

    public OssuaryBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new OssuaryModel());
    }

    // GeckoLib adds the block position to the frame tick. Negative sums break the blend.
    @Override
    public void updateAnimatedTextureFrame(OssuaryBlockEntity animatable) {
        AnimatableTexture.setAndUpdate(getTextureLocation(animatable), (int) animatable.getTick(animatable));
    }

    @Override
    public RenderType getRenderType(OssuaryBlockEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(getTextureLocation(animatable));
    }

}
