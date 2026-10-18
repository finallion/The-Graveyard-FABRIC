package com.lion.graveyard.platform.fabric;

import com.lion.graveyard.platform.ChronicleSync;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashSet;

public final class ChronicleSyncImpl {
    private ChronicleSyncImpl() {}

    public static void send(ServerPlayer player, Collection<String> found, @Nullable String newlyFound) {
        ServerPlayNetworking.send(player, new ChronicleSync(new LinkedHashSet<>(found), newlyFound));
    }
}
