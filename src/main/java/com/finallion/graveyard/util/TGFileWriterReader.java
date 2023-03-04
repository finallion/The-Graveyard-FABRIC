package com.finallion.graveyard.util;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.entities.horde.GraveyardHordeSpawner;
import com.finallion.graveyard.item.BoneStaffItem;
import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import draylar.omegaconfig.api.Config;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.resource.Resource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.World;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLOutput;
import java.util.*;

public class TGFileWriterReader {

    public TGFileWriterReader() {
    }

    public static class ServerStarted implements ServerLifecycleEvents.ServerStarted {

        public ServerStarted() {
        }

        @Override
        public void onServerStarted(MinecraftServer server) {
            /* OLD CONFIG DELETE */
            File oldConfig = new File(FabricLoader.getInstance().getConfigDir().toString() + "/the-graveyard-config.json5");
            File oldConfig2 = new File(FabricLoader.getInstance().getConfigDir().toString() + "/the-graveyard-1.19-config.json5");
            File oldConfig3 = new File(FabricLoader.getInstance().getConfigDir().toString() + "/graveyard/the-graveyard-2.0-config.json5");
            File oldConfig4 = new File(FabricLoader.getInstance().getConfigDir().toString() + "/graveyard/the-graveyard-2.2-config.json5");
            if (oldConfig.exists()) {
                if (oldConfig.delete()) {
                    TheGraveyard.LOGGER.info("Deleting old Graveyard Config file.");
                }
            }

            if (oldConfig2.exists()) {
                if (oldConfig2.delete()) {
                    TheGraveyard.LOGGER.info("Deleting old Graveyard Config file.");
                }
            }

            if (oldConfig3.exists()) {
                if (oldConfig3.delete()) {
                    TheGraveyard.LOGGER.info("Deleting old Graveyard Config file.");
                }
            }

            if (oldConfig4.exists()) {
                if (oldConfig4.delete()) {
                    TheGraveyard.LOGGER.info("Deleting old Graveyard Config file.");
                }
            }
        }
    }

    public static class Load implements ServerWorldEvents.Load {

        public Load() {
        }

        @Override
        public void onWorldLoad(MinecraftServer server, ServerWorld world) {
            readGhoulingUUIDFile(server);
            //writeConfigToJSON(server);

        }

        private void readGhoulingUUIDFile(MinecraftServer server) {
            /* GHOULING UUID FILE READER */
            String line;
            File file = new File(server.getSavePath(WorldSavePath.ROOT).toString() + "/graveyardGhoulingUUIDmapping.txt");
            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(server.getSavePath(WorldSavePath.ROOT).toString() + "/graveyardGhoulingUUIDmapping.txt"))) {
                    //TheGraveyard.LOGGER.info("Reading Graveyard Ghouling UUIDs from graveyardGhoulingUUIDMapping.txt.");
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


    public static class Unload implements ServerWorldEvents.Unload {

        public Unload() {
        }

        @Override
        public void onWorldUnload(MinecraftServer server, ServerWorld world) {
            saveGhoulingUUIDFile(server);
        }

        private void saveGhoulingUUIDFile(MinecraftServer server) {
            try {
                FileWriter fileWriter = new FileWriter(server.getSavePath(WorldSavePath.ROOT).toString() + "/graveyardGhoulingUUIDmapping.txt");
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                //TheGraveyard.LOGGER.info("Saving Graveyard Ghouling UUIDs from graveyardGhoulingUUIDMapping.txt.");

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
