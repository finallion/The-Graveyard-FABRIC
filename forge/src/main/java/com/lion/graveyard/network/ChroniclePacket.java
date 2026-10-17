package com.lion.graveyard.network;

import com.lion.graveyard.chronicle.client.ClientChronicle;
import com.lion.graveyard.platform.ChronicleSync;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Supplier;

public record ChroniclePacket(Set<String> found, @Nullable String newlyFound) {

    public static void encode(ChroniclePacket packet, FriendlyByteBuf buf) {
        ChronicleSync.write(buf, packet.found, packet.newlyFound);
    }

    public static ChroniclePacket decode(FriendlyByteBuf buf) {
        ChronicleSync.Payload payload = ChronicleSync.read(buf);
        return new ChroniclePacket(payload.found(), payload.newlyFound());
    }

    public static void handle(ChroniclePacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> ClientChronicle.receive(packet.found, packet.newlyFound));
        context.get().setPacketHandled(true);
    }
}
