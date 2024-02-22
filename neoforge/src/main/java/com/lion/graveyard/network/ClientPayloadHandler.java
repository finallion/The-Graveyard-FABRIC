package com.lion.graveyard.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
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
                })
                .exceptionally(e -> null);
    }
}
