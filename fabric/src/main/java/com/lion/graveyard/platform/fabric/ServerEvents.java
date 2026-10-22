package com.lion.graveyard.platform.fabric;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ServerEvents {

    public ServerEvents() {}

    public static class DeleteOldConfigs implements ServerLifecycleEvents.ServerStarted {

        public DeleteOldConfigs() {}

        @Override
        public void onServerStarted(MinecraftServer server) {
            List<File> oldConfigs = new ArrayList<>();
            oldConfigs.add(new File(FabricLoader.getInstance().getConfigDir().toString() + "/the-graveyard-config.json5"));
            oldConfigs.add(new File(FabricLoader.getInstance().getConfigDir().toString() + "/the-graveyard-1.19-config.json5"));
            oldConfigs.add(new File(FabricLoader.getInstance().getConfigDir().toString() + "/graveyard/the-graveyard-2.0-config.json5"));
            oldConfigs.add(new File(FabricLoader.getInstance().getConfigDir().toString() + "/graveyard/the-graveyard-2.2-config.json5"));
            oldConfigs.add(new File(FabricLoader.getInstance().getConfigDir().toString() + "/graveyard/the-graveyard-2.3-config.json5"));
            oldConfigs.add(new File(FabricLoader.getInstance().getConfigDir().toString() + "/graveyard/the-graveyard-2.4-config.json5"));
            oldConfigs.add(new File(FabricLoader.getInstance().getConfigDir().toString() + "/graveyard/the-graveyard-2.5-config.json5"));
            oldConfigs.add(new File(FabricLoader.getInstance().getConfigDir().toString() + "/graveyard/the-graveyard-2.6-config.json5"));
            oldConfigs.add(new File(FabricLoader.getInstance().getConfigDir().toString() + "/graveyard/the-graveyard-2.7-config.json5"));

            for (File file : oldConfigs) {
                if (file.exists()) {
                    file.delete();
                }
            }
        }
    }
}
