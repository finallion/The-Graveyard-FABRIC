package com.lion.graveyard.fabric;

import com.lion.graveyard.GraveyardClient;
import com.lion.graveyard.client.FragmentSpotHints;
import com.lion.graveyard.gui.GravestoneSignRenderer;
import com.lion.graveyard.gui.OssuaryScreen;
import com.lion.graveyard.init.*;
import com.lion.graveyard.particles.GraveyardFogParticle;
import com.lion.graveyard.particles.GraveyardHandParticle;
import com.lion.graveyard.particles.GraveyardSoulParticle;
import com.lion.graveyard.entities.animation.AnimationPreviewClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import com.lion.graveyard.gui.OssuaryScreen;
import com.lion.graveyard.platform.ChronicleSync;
import com.lion.graveyard.platform.OssuaryRecipesSync;
import com.lion.graveyard.chronicle.client.ClientChronicle;
import com.lion.graveyard.chronicle.client.ChronicleButton;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.PictureInPictureRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.particle.SonicBoomParticle;
import net.minecraft.client.color.block.BlockTintSources;
import java.util.List;

public class GraveyardClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        GraveyardClient.init();
        GraveyardClient.postInit();

        MenuScreens.register(TGScreens.OSSUARY_SCREEN_HANDLER, OssuaryScreen::new);
        PictureInPictureRendererRegistry.register(context -> new GravestoneSignRenderer(context.bufferSource()));
        ClientTickEvents.END_CLIENT_TICK.register(AnimationPreviewClient::clientTick);
        ClientTickEvents.END_CLIENT_TICK.register(FragmentSpotHints::clientTick);
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> ChronicleButton.addTo(screen, Screens.getWidgets(screen)::add));

        ParticleProviderRegistry.getInstance().register(TGParticles.GRAVEYARD_FOG_PARTICLE, GraveyardFogParticle.FogFactory::new);
        ParticleProviderRegistry.getInstance().register(TGParticles.GRAVEYARD_SOUL_PARTICLE, GraveyardSoulParticle.Factory::new);
        ParticleProviderRegistry.getInstance().register(TGParticles.GRAVEYARD_HAND_PARTICLE, GraveyardHandParticle.Factory::new);
        ParticleProviderRegistry.getInstance().register(TGParticles.GRAVEYARD_LEFT_HAND_PARTICLE, GraveyardHandParticle.Factory::new);
        ParticleProviderRegistry.getInstance().register(TGParticles.GRAVEYARD_SOUL_BEAM_PARTICLE, SonicBoomParticle.Provider::new);

        BlockColorRegistry.register(List.of(BlockTintSources.grassBlock()), TGBlocks.TG_GRASS_BLOCK.get());

        ClientPlayNetworking.registerGlobalReceiver(ChronicleSync.TYPE, (payload, context) -> ClientChronicle.receive(payload.found(), payload.newlyFound()));
        ClientPlayNetworking.registerGlobalReceiver(OssuaryRecipesSync.TYPE, (payload, context) -> OssuaryScreen.receive(payload));
    }
}
