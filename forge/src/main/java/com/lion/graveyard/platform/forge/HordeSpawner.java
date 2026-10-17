package com.lion.graveyard.platform.forge;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.horde.GraveyardHordeSpawner;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

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
    public static void onServerStopped(ServerStoppedEvent event) {
        spawners.clear();
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.START) {
            return;
        }

        if (event.side != LogicalSide.SERVER) {
            return;
        }

        GraveyardHordeSpawner spawner = spawners.get(event.level.dimensionTypeId().location());

        if (spawner != null) {
            ServerLevel overworld = event.level.getServer().overworld();

            spawner.tick(overworld, GraveyardHordeSpawner.monstersAllowed(overworld), true);
        }
    }
}
