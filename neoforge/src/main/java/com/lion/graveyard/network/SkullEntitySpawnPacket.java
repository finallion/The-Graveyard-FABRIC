package com.lion.graveyard.network;

import com.lion.graveyard.GraveyardClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.UUID;

public record SkullEntitySpawnPacket(Entity entity, UUID uuid, int entityID, double x, double y, double z, float pitch, float yaw) implements CustomPayload {
    public SkullEntitySpawnPacket(PacketByteBuf buf) {
        this(MinecraftClient.getInstance().world.getEntityById(buf.readInt()), buf.readUuid(), buf.readVarInt(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readByte(), buf.readByte());
    }

    @Override
    public void write(PacketByteBuf buf) {
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

    @Override
    public Identifier id() {
        return GraveyardClient.SKULL_PACKET_ID;
    }
}
