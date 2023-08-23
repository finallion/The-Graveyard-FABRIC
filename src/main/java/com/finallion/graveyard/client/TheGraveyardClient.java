package com.finallion.graveyard.client;


import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.blockentities.renders.GravestoneBlockEntityRenderer;
import com.finallion.graveyard.blockentities.renders.OssuaryBlockEntityRenderer;
import com.finallion.graveyard.blockentities.renders.SarcophagusBlockEntityRenderer;
import com.finallion.graveyard.blockentities.renders.BrazierBlockEntityRenderer;
import com.finallion.graveyard.client.gui.OssuaryScreen;
import com.finallion.graveyard.entities.models.CorruptedIllagerModel;
import com.finallion.graveyard.entities.renders.*;
import com.finallion.graveyard.init.*;
import com.finallion.graveyard.item.VialOfBlood;
import com.finallion.graveyard.network.GraveyardEntitySpawnPacket;
import com.finallion.graveyard.particles.GraveyardFogParticle;
import com.finallion.graveyard.particles.GraveyardHandParticle;
import com.finallion.graveyard.particles.GraveyardLineFogParticle;
import com.finallion.graveyard.particles.GraveyardSoulParticle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.GrassColors;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.particle.SonicBoomParticle;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;
import software.bernie.example.ClientListener;
import software.bernie.geckolib.network.GeckoLibNetwork;

import java.util.UUID;


@Environment(EnvType.CLIENT)
public class TheGraveyardClient implements ClientModInitializer {
    private static final RenderLayer CUTOUT_MIPPED = RenderLayer.getCutoutMipped();
    public static final EntityModelLayer CORRUPTED_ILLAGER_MODEL_LAYER = new EntityModelLayer(new Identifier(TheGraveyard.MOD_ID, "corrupted_illager"), "main");

    @Override
    public void onInitializeClient() {
        ParticleFactoryRegistry.getInstance().register(TGParticles.GRAVEYARD_FOG_PARTICLE, GraveyardFogParticle.FogFactory::new);
        ParticleFactoryRegistry.getInstance().register(TGParticles.GRAVEYARD_SOUL_PARTICLE, GraveyardSoulParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(TGParticles.GRAVEYARD_HAND_PARTICLE, GraveyardHandParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(TGParticles.GRAVEYARD_LEFT_HAND_PARTICLE, GraveyardHandParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(TGParticles.GRAVEYARD_SOUL_BEAM_PARTICLE, SonicBoomParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(TGParticles.GRAVEYARD_LINE_FOG_PARTICLE, GraveyardLineFogParticle.Factory::new);

        HandledScreens.register(TGScreens.OSSUARY_SCREEN_HANDLER, OssuaryScreen::new);

        BlockRenderLayerMap.INSTANCE.putBlocks(CUTOUT_MIPPED,
                TGBlocks.SKULL_WITH_RIB_CAGE,
                TGBlocks.WITHER_SKULL_WITH_RIB_CAGE,
                TGBlocks.LEANING_SKELETON,
                TGBlocks.LEANING_WITHER_SKELETON,
                TGBlocks.LYING_SKELETON,
                TGBlocks.LYING_WITHER_SKELETON,
                TGBlocks.BONE_REMAINS,
                TGBlocks.SKULL_ON_PIKE,
                TGBlocks.LATERALLY_LYING_SKELETON,
                TGBlocks.TORSO_PILE,
                TGBlocks.WITHER_BONE_REMAINS,
                TGBlocks.WITHER_SKULL_ON_PIKE,
                TGBlocks.LATERALLY_LYING_WITHER_SKELETON,
                TGBlocks.HANGED_SKELETON,
                TGBlocks.HANGED_WITHER_SKELETON,
                TGBlocks.WITHER_TORSO_PILE,
                TGBlocks.DARK_IRON_BARS,
                TGBlocks.TG_GRASS_BLOCK,
                TGBlocks.SOUL_FIRE_BRAZIER,
                TGBlocks.FIRE_BRAZIER,
                TGBlocks.CANDLE_HOLDER,
                TGBlocks.DARK_IRON_DOOR,
                TGBlocks.DARK_IRON_TRAPDOOR,
                TGBlocks.OSSUARY,

                TGBlocks.TURF,
                TGBlocks.SCARLET_HEART,
                TGBlocks.GREAT_SCARLET_HEART
        );

        BlockEntityRendererRegistry.register(TGBlocks.GRAVESTONE_BLOCK_ENTITY, GravestoneBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(TGBlocks.SARCOPHAGUS_BLOCK_ENTITY, SarcophagusBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(TGBlocks.BRAZIER_BLOCK_ENTITY, (BlockEntityRendererFactory.Context rendererDispatcherIn) -> new BrazierBlockEntityRenderer());
        BlockEntityRendererRegistry.register(TGBlocks.OSSUARY_BLOCK_ENTITY, (BlockEntityRendererFactory.Context rendererDispatcherIn) -> new OssuaryBlockEntityRenderer());

        // coloring of tg_grass_block depending on biome
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> world != null && pos != null ? BiomeColors.getGrassColor(world, pos) : GrassColors.getColor(0.5D, 1.0D),
                TGBlocks.TG_GRASS_BLOCK,
                TGBlocks.TURF);

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> GrassColors.getColor(0.5D, 1.0D),
                TGBlocks.TG_GRASS_BLOCK,
                TGBlocks.TURF);


        // entities
        EntityRendererRegistry.register(TGEntities.SKELETON_CREEPER, SkeletonCreeperRender::new);
        EntityRendererRegistry.register(TGEntities.ACOLYTE, AcolyteRender::new);
        EntityRendererRegistry.register(TGEntities.GHOUL, GhoulRenderer::new);
        EntityRendererRegistry.register(TGEntities.REAPER, ReaperRenderer::new);
        EntityRendererRegistry.register(TGEntities.REVENANT, RevenantRenderer::new);
        EntityRendererRegistry.register(TGEntities.NIGHTMARE, NightmareRenderer::new);
        EntityRendererRegistry.register(TGEntities.CORRUPTED_PILLAGER, CorruptedPillagerRenderer::new);
        EntityRendererRegistry.register(TGEntities.CORRUPTED_VINDICATOR, CorruptedVindicatorRenderer::new);
        EntityRendererRegistry.register(TGEntities.WRAITH, WraithRenderer::new);
        EntityRendererRegistry.register(TGEntities.LICH, LichRenderer::new);
        EntityRendererRegistry.register(TGEntities.FALLING_CORPSE, FallingCorpseRenderer::new);
        EntityRendererRegistry.register(TGEntities.SKULL, SkullEntityRenderer::new);
        EntityRendererRegistry.register(TGEntities.GHOULING, GhoulingRenderer::new);
        EntityRendererRegistry.register(TGEntities.NAMELESS_HANGED, NamelessHangedRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(CORRUPTED_ILLAGER_MODEL_LAYER, CorruptedIllagerModel::getTexturedModelData);

        /* CHANGING ITEM TEXTURE */
        ModelPredicateProviderRegistry.register(TGItems.VIAL_OF_BLOOD, new Identifier("charged"), (stack, world, entity, seed) -> {
            if (entity != null && stack.isOf(TGItems.VIAL_OF_BLOOD)) {
                return VialOfBlood.getBlood(stack);
            }
            return 0.0F;
        });


        // projectile packet logic
        ClientPlayNetworking.registerGlobalReceiver(GraveyardEntitySpawnPacket.ID, (client, handler, buf, responseSender) -> {
            EntityPacketOnClient.onPacket(client, buf);
        });
    }

    public class EntityPacketOnClient {
        @Environment(EnvType.CLIENT)
        public static void onPacket(MinecraftClient context, PacketByteBuf byteBuf) {
            EntityType<?> type = Registries.ENTITY_TYPE.get(byteBuf.readVarInt());
            UUID entityUUID = byteBuf.readUuid();
            int entityID = byteBuf.readVarInt();
            double x = byteBuf.readDouble();
            double y = byteBuf.readDouble();
            double z = byteBuf.readDouble();
            float pitch = (byteBuf.readByte() * 360) / 256.0F;
            float yaw = (byteBuf.readByte() * 360) / 256.0F;
            context.execute(() -> {
                ClientWorld world = MinecraftClient.getInstance().world;
                Entity entity = type.create(world);
                if (entity != null) {
                    entity.updatePosition(x, y, z);
                    entity.updateTrackedPosition(x, y, z);
                    entity.setPitch(pitch);
                    entity.setYaw(yaw);
                    entity.setId(entityID);
                    entity.setUuid(entityUUID);
                    world.addEntity(entityID, entity);
                }
            });
        }
    }
}
