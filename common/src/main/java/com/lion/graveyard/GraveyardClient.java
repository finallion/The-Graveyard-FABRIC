package com.lion.graveyard;

import com.lion.graveyard.chronicle.ChronicleOpener;
import com.lion.graveyard.chronicle.client.ChronicleScreen;
import com.lion.graveyard.client.VialChargedProperty;
import com.lion.graveyard.init.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;

public class GraveyardClient {

    @Environment(EnvType.CLIENT)
    public static void init() {
        TGEntityModelLayers.init();
        RangeSelectItemModelProperties.ID_MAPPER.put(Graveyard.id("charged"), VialChargedProperty.MAP_CODEC);
        ChronicleOpener.install(page -> {
            Minecraft client = Minecraft.getInstance();
            client.setScreen(new ChronicleScreen(client.screen, page));
        });
    }

    @Environment(EnvType.CLIENT)
    public static void postInit() {
        TGEntityRenderers.postInit();
        TGBlockEntityRenderers.postInit();
    }
}
