package com.lion.graveyard.platform.neoforge;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.horde.GraveyardHordeSpawner;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = Graveyard.MOD_ID)
public class HordeSpawner {
    private static Map<Identifier, GraveyardHordeSpawner> spawners = new HashMap<>();


    @SubscribeEvent
    public static void onServerStart(ServerStartingEvent event) {
        spawners.put(Level.OVERWORLD.identifier(), new GraveyardHordeSpawner());
    }


    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        spawners.clear();
    }

    @SubscribeEvent
    public static void onWorldTick(LevelTickEvent.Pre event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        GraveyardHordeSpawner spawner = spawners.get(level.dimension().identifier());

        if (spawner != null) {
            ServerLevel overworld = level.getServer().overworld();

            spawner.tick(overworld, GraveyardHordeSpawner.monstersAllowed(overworld));
        }
    }
}
