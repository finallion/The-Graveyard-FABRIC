package com.lion.graveyard.neoforge;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.GraveyardClient;
import com.lion.graveyard.blockentities.renders.BrazierBlockEntityRenderer;
import com.lion.graveyard.blockentities.renders.GravestoneBlockEntityRenderer;
import com.lion.graveyard.blockentities.renders.OssuaryBlockEntityRenderer;
import com.lion.graveyard.blockentities.renders.SarcophagusBlockEntityRenderer;
import com.lion.graveyard.chronicle.client.ChronicleButton;
import com.lion.graveyard.client.FragmentSpotHints;
import com.lion.graveyard.entities.animation.AnimationPreviewClient;
import com.lion.graveyard.gui.OssuaryScreen;
import com.lion.graveyard.init.TGBlockEntities;
import com.lion.graveyard.init.TGBlocks;
import com.lion.graveyard.init.TGParticles;
import com.lion.graveyard.init.TGScreens;
import com.lion.graveyard.particles.GraveyardFogParticle;
import com.lion.graveyard.particles.GraveyardHandParticle;
import com.lion.graveyard.particles.GraveyardSoulParticle;
import com.lion.graveyard.platform.neoforge.RegistryHelperImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.particle.SonicBoomParticle;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.world.level.GrassColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;

import java.util.Map;
import java.util.function.Supplier;

@EventBusSubscriber(modid = Graveyard.MOD_ID, value = Dist.CLIENT)
public class GraveyardClientNeoforge {

    @SubscribeEvent
    public static void clientInit(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            GraveyardClient.postInit();

            ItemBlockRenderTypes.setRenderLayer(TGBlocks.TG_GRASS_BLOCK.get(), ChunkSectionLayer.CUTOUT_MIPPED);

        });
    }


    @EventBusSubscriber(modid = Graveyard.MOD_ID, value = Dist.CLIENT)
    public static class GameBus {
        @SubscribeEvent
        public static void onScreenInit(ScreenEvent.Init.Post event) {
            ChronicleButton.addTo(event.getScreen(), event::addListener);
        }

        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            AnimationPreviewClient.clientTick(Minecraft.getInstance());
            FragmentSpotHints.clientTick(Minecraft.getInstance());
        }
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(TGScreens.OSSUARY_SCREEN_HANDLER, OssuaryScreen::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        for (Map.Entry<ModelLayerLocation, Supplier<LayerDefinition>> entry : RegistryHelperImpl.ENTITY_MODEL_LAYERS.entrySet()) {
            event.registerLayerDefinition(entry.getKey(), entry.getValue());
        }
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(TGBlockEntities.GRAVESTONE_BLOCK_ENTITY.get(), GravestoneBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(TGBlockEntities.SARCOPHAGUS_BLOCK_ENTITY.get(), SarcophagusBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(TGBlockEntities.BRAZIER_BLOCK_ENTITY.get(), BrazierBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(TGBlockEntities.OSSUARY_BLOCK_ENTITY.get(), OssuaryBlockEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event){
        event.registerSpriteSet(TGParticles.GRAVEYARD_FOG_PARTICLE, GraveyardFogParticle.FogFactory::new);
        event.registerSpriteSet(TGParticles.GRAVEYARD_SOUL_PARTICLE, GraveyardSoulParticle.Factory::new);
        event.registerSpriteSet(TGParticles.GRAVEYARD_HAND_PARTICLE, GraveyardHandParticle.Factory::new);
        event.registerSpriteSet(TGParticles.GRAVEYARD_LEFT_HAND_PARTICLE, GraveyardHandParticle.Factory::new);
        event.registerSpriteSet(TGParticles.GRAVEYARD_SOUL_BEAM_PARTICLE, SonicBoomParticle.Provider::new);
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        final BlockColors blockColors = event.getBlockColors();
        blockColors.register(
                (state, reader, pos, color) -> reader != null && pos != null ? BiomeColors.getAverageGrassColor(reader, pos) : GrassColor.get(0.5D, 1.0D),
                TGBlocks.TG_GRASS_BLOCK.get()
        );

    }

}
