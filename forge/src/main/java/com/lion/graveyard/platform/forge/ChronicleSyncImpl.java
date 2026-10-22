package com.lion.graveyard.platform.forge;

import com.lion.graveyard.network.ChroniclePacket;
import com.lion.graveyard.network.PacketHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashSet;

public final class ChronicleSyncImpl {
    private ChronicleSyncImpl() {}

    public static void send(ServerPlayer player, Collection<String> found, @Nullable String newlyFound) {
        PacketHandler.CHRONICLE_CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new ChroniclePacket(new LinkedHashSet<>(found), newlyFound)
        );
    }
}
