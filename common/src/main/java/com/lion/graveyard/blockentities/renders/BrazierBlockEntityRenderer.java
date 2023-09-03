package com.lion.graveyard.blockentities.renders;


import com.lion.graveyard.blockentities.BrazierBlockEntity;
import com.lion.graveyard.blockentities.models.BrazierModel;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class BrazierBlockEntityRenderer extends GeoBlockRenderer<BrazierBlockEntity> {

    public BrazierBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(new BrazierModel());
    }

    @Override
    public int getRenderDistance() {
        return 32;
    }

    @Override
    public RenderLayer getRenderType(BrazierBlockEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return RenderLayer.getEntityCutout(getTextureLocation(animatable));
    }
}
