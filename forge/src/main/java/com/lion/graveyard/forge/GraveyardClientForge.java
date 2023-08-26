package com.lion.graveyard.forge;

import com.lion.graveyard.blockentities.renders.BrazierBlockEntityRenderer;
import com.lion.graveyard.blockentities.renders.GravestoneBlockEntityRenderer;
import com.lion.graveyard.blockentities.renders.OssuaryBlockEntityRenderer;
import com.lion.graveyard.blockentities.renders.SarcophagusBlockEntityRenderer;
import com.lion.graveyard.init.TGBlockEntities;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class GraveyardClientForge {

    public GraveyardClientForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::registerBlockEntityRenderers);
    }


    @SubscribeEvent
    public void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(TGBlockEntities.GRAVESTONE_BLOCK_ENTITY.get(), GravestoneBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(TGBlockEntities.SARCOPHAGUS_BLOCK_ENTITY.get(), SarcophagusBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(TGBlockEntities.BRAZIER_BLOCK_ENTITY.get(), (BlockEntityRendererFactory.Context in) -> new BrazierBlockEntityRenderer());
        event.registerBlockEntityRenderer(TGBlockEntities.OSSUARY_BLOCK_ENTITY.get(), (BlockEntityRendererFactory.Context in) -> new OssuaryBlockEntityRenderer());
    }
}



