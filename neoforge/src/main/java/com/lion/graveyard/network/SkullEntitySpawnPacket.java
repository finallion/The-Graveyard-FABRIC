package com.lion.graveyard.network;

import com.lion.graveyard.GraveyardClient;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

import java.util.UUID;

public record SkullEntitySpawnPacket(Entity entity, UUID uuid, int entityID, double x, double y, double z, float pitch, float yaw) implements CustomPacketPayload {
    public SkullEntitySpawnPacket(FriendlyByteBuf buf) {
        this(Minecraft.getInstance().level.getEntity(buf.readInt()), buf.readUUID(), buf.readVarInt(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readByte(), buf.readByte());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(BuiltInRegistries.ENTITY_TYPE.getId(entity.getType()));
        buf.writeUUID(entity.getUUID());
        buf.writeVarInt(entity.getId());
        buf.writeDouble(entity.getX());
        buf.writeDouble(entity.getY());
        buf.writeDouble(entity.getZ());
        buf.writeByte(Mth.floor(entity.getXRot() * 256.0F / 360.0F));
        buf.writeByte(Mth.floor(entity.getYRot() * 256.0F / 360.0F));
        buf.writeFloat(entity.getXRot());
        buf.writeFloat(entity.getYRot());
    }

    @Override
    public ResourceLocation id() {
        return GraveyardClient.SKULL_PACKET_ID;
    }
}
