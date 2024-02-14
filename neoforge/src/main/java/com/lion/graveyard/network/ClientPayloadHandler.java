package com.lion.graveyard.network;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.UUID;

public class ClientPayloadHandler {
    private static final ClientPayloadHandler INSTANCE = new ClientPayloadHandler();

    public static ClientPayloadHandler getInstance() {
        return INSTANCE;
    }

    public void handleData(final SkullEntitySpawnPacket data, final PlayPayloadContext context) {
        EntityType<?> type = data.entity().getType();
        UUID entityUUID = data.uuid();
        int entityID = data.entityID();
        double x = data.x();
        double y = data.y();
        double z = data.z();
        float pitch = (data.pitch() * 360) / 256.0F;
        float yaw = (data.yaw() * 360) / 256.0F;


        context.workHandler().submitAsync(() -> {
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
                })
                .exceptionally(e -> null);
    }
}
