package com.lion.graveyard.platform;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.chronicle.Chronicle;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/// Server → client: the full set of found pages, plus the one that was just found (for the toast).
public final class ChronicleSync {
    public static final Identifier CHANNEL = new Identifier(Graveyard.MOD_ID, "chronicle");

    private ChronicleSync() {}

    @ExpectPlatform
    public static void send(ServerPlayerEntity player, Collection<String> found, @Nullable String newlyFound) {
        throw new AssertionError();
    }

    public static void write(PacketByteBuf buf, Collection<String> found, @Nullable String newlyFound) {
        List<String> valid = found.stream().filter(Chronicle::isValidId).toList();
        buf.writeVarInt(valid.size());
        valid.forEach(buf::writeString);
        boolean sendNew = Chronicle.isValidId(newlyFound);
        buf.writeBoolean(sendNew);
        if (sendNew) buf.writeString(newlyFound);
    }

    public record Payload(Set<String> found, @Nullable String newlyFound) {}

    public static Payload read(PacketByteBuf buf) {
        int count = buf.readVarInt();
        Set<String> found = new LinkedHashSet<>();
        for (int i = 0; i < count; i++) found.add(buf.readString());
        String newlyFound = buf.readBoolean() ? buf.readString() : null;
        return new Payload(found, newlyFound);
    }
}
