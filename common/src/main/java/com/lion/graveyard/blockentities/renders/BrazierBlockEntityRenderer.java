package com.lion.graveyard.blockentities.renders;


import com.lion.graveyard.blockentities.BrazierBlockEntity;
import com.lion.graveyard.blockentities.models.BrazierModel;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class BrazierBlockEntityRenderer extends GeoBlockRenderer<BrazierBlockEntity> {

    public BrazierBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new BrazierModel());
    }

    @Override
    public int getViewDistance() {
        return 32;
    }

    @Override
    public RenderType getRenderType(GeoRenderState renderState, Identifier texture) {
        return RenderTypes.entityCutout(texture);
    }
}
