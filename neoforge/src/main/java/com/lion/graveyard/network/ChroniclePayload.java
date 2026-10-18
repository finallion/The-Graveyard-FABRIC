package com.lion.graveyard.network;

import com.lion.graveyard.platform.ChronicleSync;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public record ChroniclePayload(Set<String> found, @Nullable String newlyFound) implements CustomPacketPayload {
    public static final Type<ChroniclePayload> TYPE = new Type<>(ChronicleSync.CHANNEL);

    public static final StreamCodec<RegistryFriendlyByteBuf, ChroniclePayload> CODEC = StreamCodec.of(
            (buf, payload) -> ChronicleSync.write(buf, payload.found(), payload.newlyFound()),
            buf -> {
                ChronicleSync.Payload read = ChronicleSync.read(buf);

                return new ChroniclePayload(read.found(), read.newlyFound());
            });

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
