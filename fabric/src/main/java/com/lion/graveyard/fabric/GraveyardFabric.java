package com.lion.graveyard.fabric;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.platform.fabric.HordeSpawner;
import com.lion.graveyard.platform.fabric.NamelessHangedTradeOfferResourceListener;
import com.lion.graveyard.platform.fabric.ServerEvents;
import com.lion.graveyard.util.NBTParser;
import com.lion.graveyard.util.SpawnHordeCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourceType;

import java.io.IOException;

public class GraveyardFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Graveyard.init();
        Graveyard.postInit();

        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            try {
                NBTParser.parseNBTFiles();
            } catch (IOException e) {}
        }


        registerServerEvents();
        registerCommands();

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new NamelessHangedTradeOfferResourceListener());
    }

    private void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> SpawnHordeCommand.register(dispatcher));
    }


    private void registerServerEvents() {
        ServerWorldEvents.LOAD.register(new HordeSpawner.WorldLoad());
        ServerLifecycleEvents.SERVER_STOPPED.register(new HordeSpawner.ServerStopped());
        ServerTickEvents.END_WORLD_TICK.register(new HordeSpawner.OnWorldTick());

        ServerLifecycleEvents.SERVER_STARTED.register(new ServerEvents.DeleteOldConfigs());

        ServerWorldEvents.LOAD.register(new ServerEvents.LoadUUIDMapping());
        ServerWorldEvents.UNLOAD.register(new ServerEvents.UnloadUUIDMapping());
    }
}
