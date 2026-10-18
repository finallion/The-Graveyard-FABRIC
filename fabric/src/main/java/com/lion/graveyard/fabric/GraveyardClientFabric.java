package com.lion.graveyard.fabric;

import com.lion.graveyard.GraveyardClient;
import com.lion.graveyard.client.FragmentSpotHints;
import com.lion.graveyard.gui.OssuaryScreen;
import com.lion.graveyard.init.*;
import com.lion.graveyard.item.VialOfBlood;
import com.lion.graveyard.particles.GraveyardFogParticle;
import com.lion.graveyard.particles.GraveyardHandParticle;
import com.lion.graveyard.particles.GraveyardSoulParticle;
import com.lion.graveyard.entities.animation.AnimationPreviewClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import com.lion.graveyard.platform.ChronicleSync;
import com.lion.graveyard.chronicle.client.ClientChronicle;
import com.lion.graveyard.chronicle.client.ChronicleButton;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.particle.SonicBoomParticle;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GrassColor;

public class GraveyardClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        GraveyardClient.init();
        GraveyardClient.postInit();

        MenuScreens.register(TGScreens.OSSUARY_SCREEN_HANDLER, OssuaryScreen::new);
        ClientTickEvents.END_CLIENT_TICK.register(AnimationPreviewClient::clientTick);
        ClientTickEvents.END_CLIENT_TICK.register(FragmentSpotHints::clientTick);
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> ChronicleButton.addTo(screen, Screens.getButtons(screen)::add));


        ParticleFactoryRegistry.getInstance().register(TGParticles.GRAVEYARD_FOG_PARTICLE, GraveyardFogParticle.FogFactory::new);
        ParticleFactoryRegistry.getInstance().register(TGParticles.GRAVEYARD_SOUL_PARTICLE, GraveyardSoulParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(TGParticles.GRAVEYARD_HAND_PARTICLE, GraveyardHandParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(TGParticles.GRAVEYARD_LEFT_HAND_PARTICLE, GraveyardHandParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(TGParticles.GRAVEYARD_SOUL_BEAM_PARTICLE, SonicBoomParticle.Provider::new);


        ColorProviderRegistry.BLOCK.register(
                (state, world, pos, tintIndex) -> world != null && pos != null ? BiomeColors.getAverageGrassColor(world, pos) : GrassColor.get(0.5D, 1.0D),
                TGBlocks.TG_GRASS_BLOCK.get()
        );

        ColorProviderRegistry.ITEM.register(
                (stack, tintIndex) -> GrassColor.get(0.5D, 1.0D),
                TGBlocks.TG_GRASS_BLOCK.get()
        );


        ClientPlayNetworking.registerGlobalReceiver(ChronicleSync.TYPE, (payload, context) -> ClientChronicle.receive(payload.found(), payload.newlyFound()));


        ItemProperties.register(TGItems.VIAL_OF_BLOOD.get(), ResourceLocation.withDefaultNamespace("charged"), (stack, world, entity, seed) -> {
            if (entity != null && stack.is(TGItems.VIAL_OF_BLOOD.get())) {
                return VialOfBlood.getBlood(stack);
            }

            return 0.0F;
        });
    }
}
