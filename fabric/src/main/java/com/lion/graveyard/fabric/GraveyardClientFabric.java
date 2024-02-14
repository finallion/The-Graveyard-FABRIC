package com.lion.graveyard.fabric;

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
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.GrassColors;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.particle.SonicBoomParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class GraveyardClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        GraveyardClient.init();
        GraveyardClient.postInit();

        HandledScreens.register(TGScreens.OSSUARY_SCREEN_HANDLER, OssuaryScreen::new);

        ParticleFactoryRegistry.getInstance().register(TGParticles.GRAVEYARD_FOG_PARTICLE, GraveyardFogParticle.FogFactory::new);
        ParticleFactoryRegistry.getInstance().register(TGParticles.GRAVEYARD_SOUL_PARTICLE, GraveyardSoulParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(TGParticles.GRAVEYARD_HAND_PARTICLE, GraveyardHandParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(TGParticles.GRAVEYARD_LEFT_HAND_PARTICLE, GraveyardHandParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(TGParticles.GRAVEYARD_SOUL_BEAM_PARTICLE, SonicBoomParticle.Factory::new);


        // coloring of tg_grass_block depending on biome
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> world != null && pos != null ? BiomeColors.getGrassColor(world, pos) : GrassColors.getColor(0.5D, 1.0D),
                TGBlocks.TG_GRASS_BLOCK.get(),
                TGBlocks.TURF.get());

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> GrassColors.getColor(0.5D, 1.0D),
                TGBlocks.TG_GRASS_BLOCK.get(),
                TGBlocks.TURF.get());


        ClientPlayNetworking.registerGlobalReceiver(GraveyardClient.SKULL_PACKET_ID, (client, handler, byteBuf, responseSender) -> {
            EntityType<?> type = Registries.ENTITY_TYPE.get(byteBuf.readVarInt());
            UUID entityUUID = byteBuf.readUuid();
            int entityID = byteBuf.readVarInt();
            double x = byteBuf.readDouble();
            double y = byteBuf.readDouble();
            double z = byteBuf.readDouble();
            float pitch = (byteBuf.readByte() * 360) / 256.0F;
            float yaw = (byteBuf.readByte() * 360) / 256.0F;
            client.execute(() -> {
                ClientWorld world = MinecraftClient.getInstance().world;
                Entity entity = type.create(world);
                if (entity != null) {
                    entity.updatePosition(x, y, z);
                    entity.updateTrackedPosition(x, y, z);
                    entity.setPitch(pitch);
                    entity.setYaw(yaw);
                    entity.setId(entityID);
                    entity.setUuid(entityUUID);
                    world.addEntity(entity);
                }
            });
        });

        // changing item texture
        ModelPredicateProviderRegistry.register(TGItems.VIAL_OF_BLOOD.get(), new Identifier("charged"), (stack, world, entity, seed) -> {
            if (entity != null && stack.isOf(TGItems.VIAL_OF_BLOOD.get())) {
                return VialOfBlood.getBlood(stack);
            }
            return 0.0F;
        });
    }
}
