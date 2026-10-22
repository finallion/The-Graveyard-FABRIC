package com.lion.graveyard.forge;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.GraveyardClient;
import com.lion.graveyard.client.FragmentSpotHints;
import com.lion.graveyard.chronicle.client.ChronicleButton;
import com.lion.graveyard.entities.animation.AnimationPreviewClient;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.client.event.ScreenEvent;
import com.lion.graveyard.blockentities.renders.BrazierBlockEntityRenderer;
import com.lion.graveyard.blockentities.renders.GravestoneBlockEntityRenderer;
import com.lion.graveyard.blockentities.renders.OssuaryBlockEntityRenderer;
import com.lion.graveyard.blockentities.renders.SarcophagusBlockEntityRenderer;
import com.lion.graveyard.entities.renders.*;
import com.lion.graveyard.gui.OssuaryScreen;
import com.lion.graveyard.init.*;
import com.lion.graveyard.item.VialOfBlood;
import com.lion.graveyard.particles.GraveyardFogParticle;
import com.lion.graveyard.particles.GraveyardHandParticle;
import com.lion.graveyard.particles.GraveyardSoulParticle;
import com.lion.graveyard.platform.forge.RegistryHelperImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.particle.SonicBoomParticle;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Map;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Graveyard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class GraveyardClientForge {

    @SubscribeEvent
    public static void clientInit(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            GraveyardClient.postInit();

            MenuScreens.register(TGScreens.OSSUARY_SCREEN_HANDLER, OssuaryScreen::new);

            ItemProperties.register(TGItems.VIAL_OF_BLOOD.get(), new ResourceLocation("charged"), (stack, world, entity, seed) -> {
                if (entity != null && stack.is(TGItems.VIAL_OF_BLOOD.get())) {
                    return VialOfBlood.getBlood(stack);
                }

                return 0.0F;
            });

            ItemBlockRenderTypes.setRenderLayer(TGBlocks.TG_GRASS_BLOCK.get(), RenderType.cutoutMipped());

        });
    }


    @Mod.EventBusSubscriber(modid = Graveyard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ForgeBus {
        @SubscribeEvent
        public static void onScreenInit(ScreenEvent.Init.Post event) {
            ChronicleButton.addTo(event.getScreen(), event::addListener);
        }

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                AnimationPreviewClient.clientTick(Minecraft.getInstance());
                FragmentSpotHints.clientTick(Minecraft.getInstance());
            }
        }
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        for (Map.Entry<ModelLayerLocation, Supplier<LayerDefinition>> entry : RegistryHelperImpl.ENTITY_MODEL_LAYERS.entrySet()) {
            event.registerLayerDefinition(entry.getKey(), entry.getValue());
        }
    }

    @SubscribeEvent
    public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
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

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        final BlockColors blockColors = event.getBlockColors();
        final ItemColors itemColors = event.getItemColors();

        itemColors.register((stack, color) -> {
            BlockState blockstate = ((BlockItem) stack.getItem()).getBlock().defaultBlockState();
            return blockColors.getColor(blockstate, null, null, color);
        },
                TGBlocks.TG_GRASS_BLOCK.get());

    }

}



