package com.lion.graveyard.platform;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.chronicle.Chronicle;
import dev.architectury.injectables.annotations.ExpectPlatform;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public record ChronicleSync(Set<String> found, @Nullable String newlyFound) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ChronicleSync> TYPE = new CustomPacketPayload.Type<>(Graveyard.id("chronicle"));

    public static final StreamCodec<FriendlyByteBuf, ChronicleSync> STREAM_CODEC = StreamCodec.of(ChronicleSync::write, ChronicleSync::read);

    @Override
    public CustomPacketPayload.Type<ChronicleSync> type() {
        return TYPE;
    }

    @ExpectPlatform
    public static void send(ServerPlayer player, Collection<String> found, @Nullable String newlyFound) {
        throw new AssertionError();
    }

    private static void write(FriendlyByteBuf buf, ChronicleSync payload) {
        List<String> valid = payload.found().stream()
                .filter(Chronicle::isValidId)
                .toList();
        buf.writeVarInt(valid.size());
        valid.forEach(buf::writeUtf);
        boolean sendNew = Chronicle.isValidId(payload.newlyFound());
        buf.writeBoolean(sendNew);

        if (sendNew) {
            buf.writeUtf(payload.newlyFound());
        }
    }

    private static ChronicleSync read(FriendlyByteBuf buf) {
        int count = buf.readVarInt();
        Set<String> found = new LinkedHashSet<>();
        for (int i = 0; i < count; i++) {
            found.add(buf.readUtf());
        }
        String newlyFound = buf.readBoolean() ? buf.readUtf() : null;
        return new ChronicleSync(found, newlyFound);
    }
}
