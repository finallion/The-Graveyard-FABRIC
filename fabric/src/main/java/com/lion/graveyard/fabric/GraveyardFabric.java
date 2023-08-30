package com.lion.graveyard.fabric;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.platform.fabric.HordeSpawner;
import com.lion.graveyard.platform.fabric.ServerEvents;
import com.lion.graveyard.util.SpawnHordeCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;

public class GraveyardFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Graveyard.init();
        Graveyard.postInit();

        registerServerEvents();
        registerCommands();
    }

    private void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> SpawnHordeCommand.register(dispatcher));
    }


    private static void registerServerEvents() {
        ServerWorldEvents.LOAD.register(new HordeSpawner.WorldLoad());
        ServerLifecycleEvents.SERVER_STOPPED.register(new HordeSpawner.ServerStopped());
        ServerTickEvents.END_WORLD_TICK.register(new HordeSpawner.OnWorldTick());

        ServerLifecycleEvents.SERVER_STARTED.register(new ServerEvents.DeleteOldConfigs());

        ServerWorldEvents.LOAD.register(new ServerEvents.LoadUUIDMapping());
        ServerWorldEvents.UNLOAD.register(new ServerEvents.UnloadUUIDMapping());
    }
}
