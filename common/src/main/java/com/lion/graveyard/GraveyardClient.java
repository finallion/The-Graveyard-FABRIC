package com.lion.graveyard;

import com.lion.graveyard.init.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;

public class GraveyardClient {

    public static final ResourceLocation SKULL_PACKET_ID = new ResourceLocation(Graveyard.MOD_ID, "spawn_entity");

    @Environment(EnvType.CLIENT)
    public static void init() {
        TGEntityModelLayers.init();
    }

    @Environment(EnvType.CLIENT)
    public static void postInit() {
        TGEntityRenderers.postInit();
        TGBlockEntityRenderers.postInit();
        TGRenderTypes.postInit();
    }
}
