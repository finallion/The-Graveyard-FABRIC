package com.lion.graveyard;

import com.lion.graveyard.chronicle.ChronicleOpener;
import com.lion.graveyard.chronicle.client.ChronicleScreen;
import com.lion.graveyard.init.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class GraveyardClient {

    @Environment(EnvType.CLIENT)
    public static void init() {
        TGEntityModelLayers.init();
        ChronicleOpener.install(page -> {
            MinecraftClient client = MinecraftClient.getInstance();
            client.setScreen(new ChronicleScreen(client.currentScreen, page));
        });
    }

    @Environment(EnvType.CLIENT)
    public static void postInit() {
        TGEntityRenderers.postInit();
        TGBlockEntityRenderers.postInit();
        TGRenderTypes.postInit();
    }
}
