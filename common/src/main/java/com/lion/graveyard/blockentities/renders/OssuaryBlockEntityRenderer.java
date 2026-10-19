package com.lion.graveyard.blockentities.renders;

import com.lion.graveyard.blockentities.OssuaryBlockEntity;
import com.lion.graveyard.blockentities.models.OssuaryModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class OssuaryBlockEntityRenderer extends GeoBlockRenderer<OssuaryBlockEntity> {

    public OssuaryBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new OssuaryModel());
    }

    @Override
    public RenderType getRenderType(GeoRenderState renderState, ResourceLocation texture) {
        return RenderType.entityCutout(texture);
    }

}
