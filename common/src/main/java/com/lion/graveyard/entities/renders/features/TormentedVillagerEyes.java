package com.lion.graveyard.entities.renders.features;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.TormentedVillagerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class TormentedVillagerEyes extends GeoRenderLayer<TormentedVillagerEntity> {
    private static final RenderLayer EYES = RenderLayer.getEyes(new Identifier(Graveyard.MOD_ID, "textures/entity/tormented_villager_eyes.png"));
    private static final int FULL_LIGHT = 15728640;

    private final GeoRenderer<TormentedVillagerEntity> renderer;

    public TormentedVillagerEyes(GeoRenderer<TormentedVillagerEntity> renderer) {
        super(renderer);
        this.renderer = renderer;
    }

    @Override
    public void render(MatrixStack matrices, TormentedVillagerEntity entity, BakedGeoModel model, RenderLayer renderType, VertexConsumerProvider vertexConsumers, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        this.renderer.actuallyRender(matrices, entity, model, renderType, vertexConsumers, vertexConsumers.getBuffer(EYES), true, partialTick, FULL_LIGHT, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
