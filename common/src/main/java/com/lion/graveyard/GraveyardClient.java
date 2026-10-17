package com.lion.graveyard;

import com.lion.graveyard.chronicle.ChronicleOpener;
import com.lion.graveyard.chronicle.client.ChronicleScreen;
import com.lion.graveyard.init.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

public class GraveyardClient {

    @Environment(EnvType.CLIENT)
    public static void init() {
        TGEntityModelLayers.init();
        ChronicleOpener.install(page -> {
            Minecraft client = Minecraft.getInstance();
            client.setScreen(new ChronicleScreen(client.screen, page));
        });
    }

    @Environment(EnvType.CLIENT)
    public static void postInit() {
        TGEntityRenderers.postInit();
        TGBlockEntityRenderers.postInit();
        TGRenderTypes.postInit();
    }
}
