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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public final class ChronicleSync {
    public static final ResourceLocation CHANNEL = Graveyard.id("chronicle");

    private ChronicleSync() {}

    @ExpectPlatform
    public static void send(ServerPlayer player, Collection<String> found, @Nullable String newlyFound) {
        throw new AssertionError();
    }

    public static void write(FriendlyByteBuf buf, Collection<String> found, @Nullable String newlyFound) {
        List<String> valid = found.stream()
                .filter(Chronicle::isValidId)
                .toList();
        buf.writeVarInt(valid.size());
        valid.forEach(buf::writeUtf);
        boolean sendNew = Chronicle.isValidId(newlyFound);
        buf.writeBoolean(sendNew);

        if (sendNew) {
            buf.writeUtf(newlyFound);
        }
    }

    public record Payload(Set<String> found, @Nullable String newlyFound) {}

    public static Payload read(FriendlyByteBuf buf) {
        int count = buf.readVarInt();
        Set<String> found = new LinkedHashSet<>();
        for (int i = 0; i < count; i++) {
            found.add(buf.readUtf());
        }
        String newlyFound = buf.readBoolean() ? buf.readUtf() : null;
        return new Payload(found, newlyFound);
    }
}
