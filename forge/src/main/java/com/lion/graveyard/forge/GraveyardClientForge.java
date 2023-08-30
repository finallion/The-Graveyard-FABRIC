package com.lion.graveyard.forge;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.GraveyardClient;
import com.lion.graveyard.blockentities.renders.BrazierBlockEntityRenderer;
import com.lion.graveyard.blockentities.renders.GravestoneBlockEntityRenderer;
import com.lion.graveyard.blockentities.renders.OssuaryBlockEntityRenderer;
import com.lion.graveyard.blockentities.renders.SarcophagusBlockEntityRenderer;
import com.lion.graveyard.gui.OssuaryScreen;
import com.lion.graveyard.init.*;
import com.lion.graveyard.item.VialOfBlood;
import com.lion.graveyard.particles.GraveyardFogParticle;
import com.lion.graveyard.particles.GraveyardHandParticle;
import com.lion.graveyard.particles.GraveyardSoulParticle;
import com.lion.graveyard.platform.forge.RegistryHelperImpl;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.GrassColors;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.particle.SonicBoomParticle;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Map;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Graveyard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class GraveyardClientForge {

    public GraveyardClientForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::registerBlockEntityRenderers);
        modEventBus.addListener(this::registerParticles);
        modEventBus.addListener(this::registerScreens);
        modEventBus.addListener(this::registerBlockColors);
        modEventBus.addListener(this::registerItemColors);
        modEventBus.addListener(this::registerLayerDefinitions);
    }

    @SubscribeEvent
    public void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        for (Map.Entry<EntityModelLayer, Supplier<TexturedModelData>> entry : RegistryHelperImpl.ENTITY_MODEL_LAYERS.entrySet()) {
            event.registerLayerDefinition(entry.getKey(), entry.getValue());
        }
    }

    @SubscribeEvent
    public void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(TGBlockEntities.GRAVESTONE_BLOCK_ENTITY.get(), GravestoneBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(TGBlockEntities.SARCOPHAGUS_BLOCK_ENTITY.get(), SarcophagusBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(TGBlockEntities.BRAZIER_BLOCK_ENTITY.get(), (BlockEntityRendererFactory.Context in) -> new BrazierBlockEntityRenderer());
        event.registerBlockEntityRenderer(TGBlockEntities.OSSUARY_BLOCK_ENTITY.get(), (BlockEntityRendererFactory.Context in) -> new OssuaryBlockEntityRenderer());
    }

    @SubscribeEvent
    public void registerParticles(RegisterParticleProvidersEvent event){
        event.registerSpriteSet(TGParticles.GRAVEYARD_FOG_PARTICLE, GraveyardFogParticle.FogFactory::new);
        event.registerSpriteSet(TGParticles.GRAVEYARD_SOUL_PARTICLE, GraveyardSoulParticle.Factory::new);
        event.registerSpriteSet(TGParticles.GRAVEYARD_HAND_PARTICLE, GraveyardHandParticle.Factory::new);
        event.registerSpriteSet(TGParticles.GRAVEYARD_LEFT_HAND_PARTICLE, GraveyardHandParticle.Factory::new);
        event.registerSpriteSet(TGParticles.GRAVEYARD_SOUL_BEAM_PARTICLE, SonicBoomParticle.Factory::new);
    }

    @SubscribeEvent
    public void registerScreens(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            GraveyardClient.postInit();

            HandledScreens.register(TGScreens.OSSUARY_SCREEN_HANDLER, OssuaryScreen::new);

            ModelPredicateProviderRegistry.register(TGItems.VIAL_OF_BLOOD.get(), new Identifier("charged"), (stack, world, entity, seed) -> {
                if (entity != null && stack.isOf(TGItems.VIAL_OF_BLOOD.get())) {
                    return VialOfBlood.getBlood(stack);
                }
                return 0.0F;
            });
        });
    }

    @SubscribeEvent
    public void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        final BlockColors blockColors = event.getBlockColors();
        blockColors.registerColorProvider((state, reader, pos, color) -> reader != null && pos != null ? BiomeColors.getGrassColor(reader, pos) : GrassColors.getColor(0.5D, 1.0D),
                TGBlocks.TG_GRASS_BLOCK.get(),
                TGBlocks.TURF.get());

    }

    @SubscribeEvent
    public void registerItemColors(RegisterColorHandlersEvent.Item event) {
        final BlockColors blockColors = event.getBlockColors();
        final ItemColors itemColors = event.getItemColors();

        itemColors.register((stack, color) -> {
            BlockState blockstate = ((BlockItem) stack.getItem()).getBlock().getDefaultState();
            return blockColors.getColor(blockstate, null, null, color);
        },
                TGBlocks.TG_GRASS_BLOCK.get(),
                TGBlocks.TURF.get());

    }

}



