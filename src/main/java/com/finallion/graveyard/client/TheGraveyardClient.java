package com.finallion.graveyard.client;


import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.blockentities.renders.GravestoneBlockEntityRenderer;
import com.finallion.graveyard.blockentities.renders.SarcophagusBlockEntityRenderer;
import com.finallion.graveyard.blockentities.renders.BrazierBlockEntityRenderer;
import com.finallion.graveyard.entities.models.CorruptedIllagerModel;
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
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.GrassColors;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
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
    public static final EntityModelLayer CORRUPTED_ILLAGER_MODEL_LAYER = new EntityModelLayer(new Identifier(TheGraveyard.MOD_ID, "corrupted_illager"), "main");

    @Override
    public void onInitializeClient() {

        TGParticles.init();

        BlockRenderLayerMap.INSTANCE.putBlocks(CUTOUT_MIPPED,
                TGBlocks.SKULL_WITH_RIB_CAGE,
                TGBlocks.WITHER_SKULL_WITH_RIB_CAGE,
                TGBlocks.LEANING_SKELETON,
                TGBlocks.LEANING_WITHER_SKELETON,
                TGBlocks.LYING_SKELETON,
                TGBlocks.LYING_WITHER_SKELETON,
                TGBlocks.DARK_IRON_BARS,
                TGBlocks.TG_GRASS_BLOCK,
                TGBlocks.SOUL_FIRE_BRAZIER,
                TGBlocks.FIRE_BRAZIER,
                TGBlocks.CANDLE_HOLDER,
                TGBlocks.DARK_IRON_DOOR,
                TGBlocks.DARK_IRON_TRAPDOOR);

        BlockEntityRendererRegistry.INSTANCE.register(TGBlocks.GRAVESTONE_BLOCK_ENTITY, GravestoneBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(TGBlocks.SARCOPHAGUS_BLOCK_ENTITY, SarcophagusBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(TGBlocks.BRAZIER_BLOCK_ENTITY, (BlockEntityRendererFactory.Context rendererDispatcherIn) -> new BrazierBlockEntityRenderer());

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
        EntityRendererRegistry.INSTANCE.register(TGEntities.CORRUPTED_PILLAGER, CorruptedPillagerRenderer::new);
        EntityRendererRegistry.INSTANCE.register(TGEntities.CORRUPTED_VINDICATOR, CorruptedVindicatorRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(CORRUPTED_ILLAGER_MODEL_LAYER, CorruptedIllagerModel::getTexturedModelData);

        // register block bench model
        ModelLoadingRegistry.INSTANCE.registerModelProvider((ResourceManager manager, Consumer<Identifier> out) -> {
            out.accept(SarcophagusBlockEntityRenderer.SARCOPHAGUS_FOOT);
            out.accept(SarcophagusBlockEntityRenderer.SARCOPHAGUS_HEAD);
            out.accept(SarcophagusBlockEntityRenderer.SARCOPHAGUS_FOOT_LID);
            out.accept(SarcophagusBlockEntityRenderer.SARCOPHAGUS_HEAD_LID);

            for (Block block : TGBlocks.coffins) {
                String woodType = block.getTranslationKey().split("\\.")[2];
                out.accept(new Identifier(TheGraveyard.MOD_ID, "block/" + woodType + "_foot"));
                out.accept(new Identifier(TheGraveyard.MOD_ID, "block/" + woodType + "_foot_lid"));
                out.accept(new Identifier(TheGraveyard.MOD_ID, "block/" + woodType + "_head"));
                out.accept(new Identifier(TheGraveyard.MOD_ID, "block/" + woodType + "_head_lid"));
            }

        });

    }
}
