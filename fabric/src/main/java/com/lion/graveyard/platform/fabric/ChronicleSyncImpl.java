package com.lion.graveyard.platform.fabric;

import com.lion.graveyard.platform.ChronicleSync;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public final class ChronicleSyncImpl {
    private ChronicleSyncImpl() {}

    public static void send(ServerPlayer player, Collection<String> found, @Nullable String newlyFound) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        ChronicleSync.write(buf, found, newlyFound);
        ServerPlayNetworking.send(player, ChronicleSync.CHANNEL, buf);
    }
}
