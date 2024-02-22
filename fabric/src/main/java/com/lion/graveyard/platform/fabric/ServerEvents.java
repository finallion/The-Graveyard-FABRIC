package com.lion.graveyard.platform.fabric;

import com.lion.graveyard.item.BoneStaffItem;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.LevelResource;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class ServerEvents {

    public ServerEvents() {
    }

    public static class DeleteOldConfigs implements ServerLifecycleEvents.ServerStarted {

        public DeleteOldConfigs() {
        }

        @Override
        public void onServerStarted(MinecraftServer server) {
            /* OLD CONFIG DELETE */
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
                if (file.exists()) file.delete();
            }
        }
    }

    public static class LoadUUIDMapping implements ServerWorldEvents.Load {

        public LoadUUIDMapping() {
        }

        @Override
        public void onWorldLoad(MinecraftServer server, ServerLevel world) {
            readGhoulingUUIDFile(server);
        }

        private void readGhoulingUUIDFile(MinecraftServer server) {
            /* GHOULING UUID FILE READER */
            String line;
            File file = new File(server.getWorldPath(LevelResource.ROOT).toString() + "/graveyardGhoulingUUIDmapping.txt");
            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(server.getWorldPath(LevelResource.ROOT).toString() + "/graveyardGhoulingUUIDmapping.txt"))) {
                    while ((line = reader.readLine()) != null) {
                        String[] keyValuePair = line.split(":", 2);

                        if (keyValuePair.length > 1) {
                            String key = keyValuePair[0];
                            String value = keyValuePair[1];
                            BoneStaffItem.ownerGhoulingMapping.putIfAbsent(UUID.fromString(key), UUID.fromString(value));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class UnloadUUIDMapping implements ServerWorldEvents.Unload {

        public UnloadUUIDMapping() {
        }

        @Override
        public void onWorldUnload(MinecraftServer server, ServerLevel world) {
            saveGhoulingUUIDFile(server);
        }

        private void saveGhoulingUUIDFile(MinecraftServer server) {
            try {
                FileWriter fileWriter = new FileWriter(server.getWorldPath(LevelResource.ROOT).toString() + "/graveyardGhoulingUUIDmapping.txt");
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                for (Map.Entry<UUID, UUID> entry : BoneStaffItem.ownerGhoulingMapping.entrySet()) {
                    bufferedWriter.write(entry.getKey() + ":" + entry.getValue());
                    bufferedWriter.newLine();
                }

                bufferedWriter.flush();
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}


