package com.lion.graveyard.platform.fabric;

import com.lion.graveyard.platform.ChronicleSync;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public final class ChronicleSyncImpl {
    private ChronicleSyncImpl() {}

    public static void send(ServerPlayerEntity player, Collection<String> found, @Nullable String newlyFound) {
        PacketByteBuf buf = PacketByteBufs.create();
        ChronicleSync.write(buf, found, newlyFound);
        ServerPlayNetworking.send(player, ChronicleSync.CHANNEL, buf);
    }
}
