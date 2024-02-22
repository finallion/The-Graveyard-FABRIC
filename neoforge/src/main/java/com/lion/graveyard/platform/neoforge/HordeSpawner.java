package com.lion.graveyard.platform.neoforge;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.horde.GraveyardHordeSpawner;
import net.minecraft.server.MinecraftServer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

import java.util.HashMap;
import java.util.Map;


@Mod.EventBusSubscriber(modid = Graveyard.MOD_ID)
public class HordeSpawner {
    private static Map<ResourceLocation, GraveyardHordeSpawner> spawners = new HashMap<>();


    @SubscribeEvent
    public static void onServerStart(ServerStartingEvent event) {
        spawners.put(Level.OVERWORLD.location(), new GraveyardHordeSpawner());
    }

    @SubscribeEvent
    public static void onWorldLoad(LevelEvent.Load event) {
        MinecraftServer server = event.getLevel().getServer();

        if (server != null) {
            new ServerEvents.Load().onWorldLoad(server);
        }
    }

    @SubscribeEvent
    public static void onWorldUnload(LevelEvent.Unload event) {
        MinecraftServer server = event.getLevel().getServer();

        if (server != null) {
            new ServerEvents.Unload().onWorldUnload(server);
        }
    }


    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        spawners.clear();
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.START)
            return;

        if (event.side != LogicalSide.SERVER)
            return;

        GraveyardHordeSpawner spawner = spawners.get(event.level.dimension().location());
        if (spawner != null) {
            spawner.tick(event.level.getServer().overworld(), true, true);
        }
    }
}
