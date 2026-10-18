package com.lion.graveyard.platform.neoforge;

import com.lion.graveyard.network.ChroniclePayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashSet;

public final class ChronicleSyncImpl {
    private ChronicleSyncImpl() {}

    public static void send(ServerPlayer player, Collection<String> found, @Nullable String newlyFound) {
        PacketDistributor.sendToPlayer(player, new ChroniclePayload(new LinkedHashSet<>(found), newlyFound));
    }
}
