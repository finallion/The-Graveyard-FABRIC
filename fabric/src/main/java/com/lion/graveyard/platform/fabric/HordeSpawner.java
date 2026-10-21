package com.lion.graveyard.platform.fabric;

import com.lion.graveyard.entities.horde.GraveyardHordeSpawner;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HordeSpawner {
    private static Map<Identifier, ArrayList<GraveyardHordeSpawner>> spawners = new HashMap<>();
    private static boolean loaded = false;

    public HordeSpawner(){}

    public static class WorldLoad implements ServerLevelEvents.Load {
        public WorldLoad() {}

        @Override
        public void onLevelLoad(MinecraftServer server, ServerLevel world) {
            if (loaded) {
                return;
            }

            addToSpawners(Level.OVERWORLD.identifier(), new GraveyardHordeSpawner());
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

    public static class OnWorldTick implements ServerTickEvents.EndLevelTick {

        @Override
        public void onEndTick(ServerLevel world) {
            if (world.isClientSide()) {
                return;
            }

            ArrayList<GraveyardHordeSpawner> spawnerList = spawners.get(world.dimension().identifier());

            if (spawnerList != null) {
                for (var spawner : spawnerList) {
                    if (spawner != null) {
                        spawner.tick(world, GraveyardHordeSpawner.monstersAllowed(world));
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
