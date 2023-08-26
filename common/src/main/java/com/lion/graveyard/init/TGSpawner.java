package com.lion.graveyard.init;

import com.finallion.graveyard.entities.horde.GraveyardHordeSpawner;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TGSpawner {
    private static Map<Identifier, ArrayList<GraveyardHordeSpawner>> spawners = new HashMap<>();
    private static boolean loaded = false;

    public TGSpawner(){}

    public static class WorldLoad implements ServerWorldEvents.Load {
        public WorldLoad() {}

        @Override
        public void onWorldLoad(MinecraftServer server, ServerWorld world) {
            if (loaded) {
                return;
            }
            addToSpawners(World.OVERWORLD.getValue(), new GraveyardHordeSpawner());
            loaded = true;
        }
    }
    public static class ServerStopped implements ServerLifecycleEvents.ServerStopped {
        public ServerStopped(){}

        @Override
        public void onServerStopped(MinecraftServer server) {
            loaded = false;
            spawners.clear();
        }
    }

    public static class OnWorldTick implements ServerTickEvents.EndWorldTick {

        @Override
        public void onEndTick(ServerWorld world) {
            if (world.isClient()) {
                return;
            }

            ArrayList<GraveyardHordeSpawner> spawnerList = spawners.get(world.getRegistryKey().getValue());
            if (spawnerList != null) {
                for (var spawner : spawnerList) {
                    if (spawner != null) {
                        spawner.spawn(world, true, true);
                        //spawner.tick(world);
                    }
                }
            }
        }
    }

    public static Map<Identifier, ArrayList<GraveyardHordeSpawner>> getSpawners() {
        return spawners;
    }

    public static void addToSpawners(Identifier world, GraveyardHordeSpawner spawner) {
        if (!spawners.containsKey(world)) {
            spawners.put(world, new ArrayList<>());
        }

        spawners.get(world).add(spawner);
    }
}
