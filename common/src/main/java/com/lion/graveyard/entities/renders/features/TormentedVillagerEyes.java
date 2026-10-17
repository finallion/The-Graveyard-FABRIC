package com.lion.graveyard.entities.renders.features;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.TormentedVillagerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class TormentedVillagerEyes extends GeoRenderLayer<TormentedVillagerEntity> {
    private static final RenderType EYES = RenderType.eyes(Graveyard.id("textures/entity/tormented_villager_eyes.png"));
    private static final int FULL_LIGHT = 15728640;

    private final GeoRenderer<TormentedVillagerEntity> renderer;

    public TormentedVillagerEyes(GeoRenderer<TormentedVillagerEntity> renderer) {
        super(renderer);
        this.renderer = renderer;
    }

    @Override
    public void render(PoseStack matrices, TormentedVillagerEntity entity, BakedGeoModel model, RenderType renderType, MultiBufferSource vertexConsumers, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        this.renderer.actuallyRender(matrices, entity, model, renderType, vertexConsumers, vertexConsumers.getBuffer(EYES), true, partialTick, FULL_LIGHT, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
