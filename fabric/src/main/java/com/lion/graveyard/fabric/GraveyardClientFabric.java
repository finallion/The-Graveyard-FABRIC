package com.lion.graveyard.fabric;

import com.lion.graveyard.GraveyardClient;
import com.lion.graveyard.blockentities.renders.BrazierBlockEntityRenderer;
import com.lion.graveyard.blockentities.renders.GravestoneBlockEntityRenderer;
import com.lion.graveyard.blockentities.renders.OssuaryBlockEntityRenderer;
import com.lion.graveyard.blockentities.renders.SarcophagusBlockEntityRenderer;
import com.lion.graveyard.init.TGBlockEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;

public class GraveyardClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        GraveyardClient.init();

        BlockEntityRendererRegistry.register(TGBlockEntities.GRAVESTONE_BLOCK_ENTITY.get(), GravestoneBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(TGBlockEntities.SARCOPHAGUS_BLOCK_ENTITY.get(), SarcophagusBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(TGBlockEntities.BRAZIER_BLOCK_ENTITY.get(), (BlockEntityRendererFactory.Context rendererDispatcherIn) -> new BrazierBlockEntityRenderer());
        BlockEntityRendererRegistry.register(TGBlockEntities.OSSUARY_BLOCK_ENTITY.get(), (BlockEntityRendererFactory.Context rendererDispatcherIn) -> new OssuaryBlockEntityRenderer());

    }
}
