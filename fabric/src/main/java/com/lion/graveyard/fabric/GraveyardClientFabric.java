package com.lion.graveyard.fabric;

import com.lion.graveyard.GraveyardClient;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SonicBoomParticle;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.GrassColor;

import java.util.UUID;

public class GraveyardClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        GraveyardClient.init();
        GraveyardClient.postInit();

        MenuScreens.register(TGScreens.OSSUARY_SCREEN_HANDLER, OssuaryScreen::new);

        ParticleFactoryRegistry.getInstance().register(TGParticles.GRAVEYARD_FOG_PARTICLE, GraveyardFogParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(TGParticles.GRAVEYARD_SOUL_PARTICLE, GraveyardSoulParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(TGParticles.GRAVEYARD_HAND_PARTICLE, GraveyardHandParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(TGParticles.GRAVEYARD_LEFT_HAND_PARTICLE, GraveyardHandParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(TGParticles.GRAVEYARD_SOUL_BEAM_PARTICLE, SonicBoomParticle.Provider::new);


        // coloring of tg_grass_block depending on biome
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> world != null && pos != null ? BiomeColors.getAverageGrassColor(world, pos) : GrassColor.get(0.5D, 1.0D),
                TGBlocks.TG_GRASS_BLOCK.get(),
                TGBlocks.TURF.get());

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> GrassColor.get(0.5D, 1.0D),
                TGBlocks.TG_GRASS_BLOCK.get(),
                TGBlocks.TURF.get());


        ClientPlayNetworking.registerGlobalReceiver(GraveyardClient.SKULL_PACKET_ID, (client, handler, byteBuf, responseSender) -> {
            EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.byId(byteBuf.readVarInt());
            UUID entityUUID = byteBuf.readUUID();
            int entityID = byteBuf.readVarInt();
            double x = byteBuf.readDouble();
            double y = byteBuf.readDouble();
            double z = byteBuf.readDouble();
            float pitch = (byteBuf.readByte() * 360) / 256.0F;
            float yaw = (byteBuf.readByte() * 360) / 256.0F;
            client.execute(() -> {
                ClientLevel world = Minecraft.getInstance().level;
                Entity entity = type.create(world);
                if (entity != null) {
                    entity.setPos(x, y, z);
                    entity.setPosRaw(x, y, z);
                    entity.setXRot(pitch);
                    entity.setYRot(yaw);
                    entity.setId(entityID);
                    entity.setUUID(entityUUID);
                    world.addEntity(entity);
                }
            });
        });

        // changing item texture
        ItemProperties.register(TGItems.VIAL_OF_BLOOD.get(), new ResourceLocation("charged"), (stack, world, entity, seed) -> {
            if (entity != null && stack.is(TGItems.VIAL_OF_BLOOD.get())) {
                return VialOfBlood.getBlood(stack);
            }
            return 0.0F;
        });
    }
}
