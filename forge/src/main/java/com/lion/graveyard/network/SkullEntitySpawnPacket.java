package com.lion.graveyard.network;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SkullEntitySpawnPacket {
    private ClientWorld client;
    private Entity entity;
    private UUID entityUUID;
    private int entityID;
    private double x;
    private double y;
    private double z;
    private float pitch;
    private float yaw;

    public SkullEntitySpawnPacket(PacketByteBuf buf) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        this.client = minecraftClient.world;

        this.entity = minecraftClient.world.getEntityById(buf.readInt());
        this.entityUUID = buf.readUuid();
        this.entityID = buf.readVarInt();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.pitch = buf.readByte();
        this.yaw = buf.readByte();
    }

    public SkullEntitySpawnPacket(Entity entity, UUID uuid, int entityID, double x, double y, double z, float pitch, float yaw) {
        this.entity = entity;
        this.entityUUID = uuid;
        this.entityID = entityID;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public void encode(PacketByteBuf buf) {
        buf.writeVarInt(Registries.ENTITY_TYPE.getRawId(entity.getType()));
        buf.writeUuid(entity.getUuid());
        buf.writeVarInt(entity.getId());
        buf.writeDouble(entity.getX());
        buf.writeDouble(entity.getY());
        buf.writeDouble(entity.getZ());
        buf.writeByte(MathHelper.floor(entity.getPitch() * 256.0F / 360.0F));
        buf.writeByte(MathHelper.floor(entity.getYaw() * 256.0F / 360.0F));
        buf.writeFloat(entity.getPitch());
        buf.writeFloat(entity.getYaw());
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            ClientWorld world = MinecraftClient.getInstance().world;
            if (entity != null) {
                entity.updatePosition(x, y, z);
                entity.updateTrackedPosition(x, y, z);
                entity.setPitch(pitch);
                entity.setYaw(yaw);
                entity.setId(entityID);
                entity.setUuid(entityUUID);
                world.addEntity(entityID, entity);
            }
        }));

        context.get().setPacketHandled(true);

    }

}
