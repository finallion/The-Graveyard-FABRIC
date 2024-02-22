package com.lion.graveyard.neoforge;

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
import com.lion.graveyard.platform.neoforge.RegistryHelperImpl;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.particle.SonicBoomParticle;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

import java.util.Map;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Graveyard.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class GraveyardClientNeoforge {


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

            //RenderLayers.setRenderLayer(TGBlocks.TG_GRASS_BLOCK.get(), RenderType.cutoutMipped());

        });
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
        //event.registerBlockEntityRenderer(TGBlockEntities.BRAZIER_BLOCK_ENTITY.get(), context -> new BrazierBlockEntityRenderer());
        //event.registerBlockEntityRenderer(TGBlockEntities.OSSUARY_BLOCK_ENTITY.get(), context -> new OssuaryBlockEntityRenderer());
    }

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event){
        event.registerSpriteSet(TGParticles.GRAVEYARD_FOG_PARTICLE, GraveyardFogParticle.Provider::new);
        event.registerSpriteSet(TGParticles.GRAVEYARD_SOUL_PARTICLE, GraveyardSoulParticle.Provider::new);
        event.registerSpriteSet(TGParticles.GRAVEYARD_HAND_PARTICLE, GraveyardHandParticle.Provider::new);
        event.registerSpriteSet(TGParticles.GRAVEYARD_LEFT_HAND_PARTICLE, GraveyardHandParticle.Provider::new);
        event.registerSpriteSet(TGParticles.GRAVEYARD_SOUL_BEAM_PARTICLE, SonicBoomParticle.Provider::new);
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        final BlockColors blockColors = event.getBlockColors();
        blockColors.register((state, reader, pos, color) -> reader != null && pos != null ? BiomeColors.getAverageGrassColor(reader, pos) : GrassColor.get(0.5D, 1.0D),
                TGBlocks.TG_GRASS_BLOCK.get(),
                TGBlocks.TURF.get());

    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        final BlockColors blockColors = event.getBlockColors();
        final ItemColors itemColors = event.getItemColors();

        itemColors.register((stack, color) -> {
            BlockState blockstate = ((BlockItem) stack.getItem()).getBlock().defaultBlockState();
            return blockColors.getColor(blockstate, null, null, color);
        },
                TGBlocks.TG_GRASS_BLOCK.get(),
                TGBlocks.TURF.get());

    }

}



