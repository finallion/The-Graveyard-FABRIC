package com.lion.graveyard.entities.renders;

import com.lion.graveyard.entities.TormentedVillagerEntity;
import com.lion.graveyard.entities.models.TormentedVillagerModel;
import com.lion.graveyard.entities.renders.features.TormentedVillagerEyes;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TormentedVillagerRenderer extends GeoEntityRenderer<TormentedVillagerEntity> {

    public TormentedVillagerRenderer(EntityRendererFactory.Context context) {
        super(context, new TormentedVillagerModel());
        this.addRenderLayer(new TormentedVillagerEyes(this));
        this.shadowRadius = 0.4F;
    }

    @Override
    protected float getDeathMaxRotation(TormentedVillagerEntity entity) {
        return 0.0F;
    }

    @Override
    public RenderLayer getRenderType(TormentedVillagerEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return RenderLayer.getEntityTranslucent(texture);
    }
}
