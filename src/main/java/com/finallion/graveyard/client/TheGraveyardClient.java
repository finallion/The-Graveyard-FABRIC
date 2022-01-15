package com.finallion.graveyard.client;


import com.finallion.graveyard.blockentities.SarcophagusBlockEntity;
import com.finallion.graveyard.blockentities.render.GravestoneBlockEntityRenderer;
import com.finallion.graveyard.blockentities.render.SarcophagusBlockEntityRenderer;
import com.finallion.graveyard.entities.renders.*;
import com.finallion.graveyard.init.TGBlocks;
import com.finallion.graveyard.init.TGEntities;
import com.finallion.graveyard.init.TGParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.GrassColors;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;


@Environment(EnvType.CLIENT)
public class TheGraveyardClient implements ClientModInitializer {
    private static final RenderLayer CUTOUT_MIPPED = RenderLayer.getCutoutMipped();

    @Override
    public void onInitializeClient() {

        TGParticles.init();

        BlockRenderLayerMap.INSTANCE.putBlocks(CUTOUT_MIPPED, TGBlocks.DARK_IRON_BARS, TGBlocks.TG_GRASS_BLOCK);

        BlockEntityRendererRegistry.INSTANCE.register(TGBlocks.GRAVESTONE_BLOCK_ENTITY, GravestoneBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(TGBlocks.SARCOPHAGUS_BLOCK_ENTITY, SarcophagusBlockEntityRenderer::new);

        // coloring of tg_grass_block depending on biome
        ColorProviderRegistry.BLOCK.register(new BlockColorProvider() {
            @Override
            public int getColor(BlockState state, @Nullable BlockRenderView world, @Nullable BlockPos pos, int tintIndex) {
                return world != null && pos != null ? BiomeColors.getGrassColor(world, pos) : GrassColors.getColor(0.5D, 1.0D);
            }
        }, TGBlocks.TG_GRASS_BLOCK);

        ColorProviderRegistry.ITEM.register(new ItemColorProvider() {
            @Override
            public int getColor(ItemStack stack, int tintIndex) {
                return GrassColors.getColor(0.5D, 1.0D);
            }
        }, TGBlocks.TG_GRASS_BLOCK);


        // entities
        EntityRendererRegistry.INSTANCE.register(TGEntities.SKELETON_CREEPER, SkeletonCreeperRender::new);
        EntityRendererRegistry.INSTANCE.register(TGEntities.ACOLYTE, AcolyteRender::new);
        EntityRendererRegistry.INSTANCE.register(TGEntities.GHOUL, GhoulRenderer::new);
        EntityRendererRegistry.INSTANCE.register(TGEntities.REAPER, ReaperRenderer::new);
        EntityRendererRegistry.INSTANCE.register(TGEntities.REVENANT, RevenantRenderer::new);
        EntityRendererRegistry.INSTANCE.register(TGEntities.NIGHTMARE, NightmareRenderer::new);

        // register block bench model
        ModelLoadingRegistry.INSTANCE.registerModelProvider((ResourceManager manager, Consumer<Identifier> out) -> {
            out.accept(SarcophagusBlockEntityRenderer.SARCOPHAGUS_FOOT);
            out.accept(SarcophagusBlockEntityRenderer.SARCOPHAGUS_HEAD);
        });

    }
}
