package com.lion.graveyard.blockentities.renders;

import com.lion.graveyard.blockentities.OssuaryBlockEntity;
import com.lion.graveyard.blockentities.models.OssuaryModel;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class OssuaryBlockEntityRenderer extends GeoBlockRenderer<OssuaryBlockEntity> {

    public OssuaryBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(new OssuaryModel());
    }

    @Override
    public RenderLayer getRenderType(OssuaryBlockEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return RenderLayer.getEntityCutout(getTextureLocation(animatable));
    }

}
