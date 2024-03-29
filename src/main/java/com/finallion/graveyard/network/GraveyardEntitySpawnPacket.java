package com.finallion.graveyard.network;

import com.finallion.graveyard.TheGraveyard;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class GraveyardEntitySpawnPacket {
    public static final Identifier ID = new Identifier(TheGraveyard.MOD_ID, "spawn_entity");

    public static Packet<ClientPlayPacketListener> createPacket(Entity entity) {
        PacketByteBuf buf = createBuffer();
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
        return ServerPlayNetworking.createS2CPacket(ID, buf);
    }

    private static PacketByteBuf createBuffer() {
        return new PacketByteBuf(Unpooled.buffer());
    }
}