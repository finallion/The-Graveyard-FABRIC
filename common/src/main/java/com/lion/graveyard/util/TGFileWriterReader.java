package com.lion.graveyard.util;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.*;
import java.util.Map;
import java.util.UUID;

public class TGFileWriterReader {

    public TGFileWriterReader() {}

    public static class Load {

        public void onWorldLoad(MinecraftServer server) {
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
                            main.java.com.lion.graveyard.item.BoneStaffItem.ownerGhoulingMapping.putIfAbsent(UUID.fromString(key), UUID.fromString(value));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static class Unload {

        public void onWorldUnload(MinecraftServer server) {
            try {
                FileWriter fileWriter = new FileWriter(server.getWorldPath(LevelResource.ROOT).toString() + "/graveyardGhoulingUUIDmapping.txt");
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                for (Map.Entry<UUID, UUID> entry : main.java.com.lion.graveyard.item.BoneStaffItem.ownerGhoulingMapping.entrySet()) {
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
