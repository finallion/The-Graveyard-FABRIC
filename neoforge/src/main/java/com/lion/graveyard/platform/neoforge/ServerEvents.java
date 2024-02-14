package com.lion.graveyard.platform.neoforge;

import com.lion.graveyard.item.BoneStaffItem;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;

import java.io.*;
import java.util.Map;
import java.util.UUID;

public class ServerEvents {

    public static class Load {

        public void onWorldLoad(MinecraftServer server) {
            /* GHOULING UUID FILE READER */
            String line;
            File file = new File(server.getSavePath(WorldSavePath.ROOT).toString() + "/graveyardGhoulingUUIDmapping.txt");
            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(server.getSavePath(WorldSavePath.ROOT).toString() + "/graveyardGhoulingUUIDmapping.txt"))) {
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

    public static class Unload {

        public void onWorldUnload(MinecraftServer server) {
            try {
                FileWriter fileWriter = new FileWriter(server.getSavePath(WorldSavePath.ROOT).toString() + "/graveyardGhoulingUUIDmapping.txt");
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
