package com.lion.graveyard.chronicle.client;

import com.lion.graveyard.Graveyard;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

@Environment(EnvType.CLIENT)
public final class ClientChronicle {
    private static Set<String> found = new LinkedHashSet<>();

    private ClientChronicle() {}

    public static void receive(Set<String> pages, @Nullable String newlyFound) {
        found = new LinkedHashSet<>(pages);

        if (newlyFound != null && Graveyard.getConfig().flag("chronicleToasts", true)) {
            Minecraft client = Minecraft.getInstance();
            client.getToastManager().addToast(new ChronicleToast(newlyFound));
        }
    }

    public static Set<String> found() {
        return Collections.unmodifiableSet(found);
    }

    public static boolean isFound(String page) {
        return found.contains(page);
    }
}
