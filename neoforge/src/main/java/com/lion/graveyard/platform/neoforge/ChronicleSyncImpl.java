package com.lion.graveyard.platform.neoforge;

import com.lion.graveyard.platform.ChronicleSync;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashSet;

public final class ChronicleSyncImpl {
    private ChronicleSyncImpl() {}

    public static void send(ServerPlayer player, Collection<String> found, @Nullable String newlyFound) {
        PacketDistributor.sendToPlayer(player, new ChronicleSync(new LinkedHashSet<>(found), newlyFound));
    }
}
