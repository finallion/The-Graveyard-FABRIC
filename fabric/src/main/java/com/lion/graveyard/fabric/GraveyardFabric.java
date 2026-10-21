package com.lion.graveyard.fabric;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.platform.ChronicleSync;
import com.lion.graveyard.platform.OssuaryRecipesSync;
import com.lion.graveyard.platform.fabric.HordeSpawner;
import com.lion.graveyard.platform.fabric.NamelessHangedTradeOfferResourceListener;
import com.lion.graveyard.platform.fabric.ServerEvents;
import com.lion.graveyard.util.SpawnHordeCommand;
import com.lion.graveyard.util.ChronicleCommand;
import com.lion.graveyard.chronicle.Chronicle;
import com.lion.graveyard.event.CommonEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.InteractionResult;


public class GraveyardFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Graveyard.init();
        Graveyard.postInit();

        PayloadTypeRegistry.clientboundPlay().register(ChronicleSync.TYPE, ChronicleSync.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(OssuaryRecipesSync.TYPE, OssuaryRecipesSync.STREAM_CODEC);

        registerServerEvents();
        registerCommands();

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new NamelessHangedTradeOfferResourceListener());
    }

    private void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            SpawnHordeCommand.register(dispatcher);
            ChronicleCommand.register(dispatcher);
        });
    }


    private void registerServerEvents() {
        ServerLevelEvents.LOAD.register(new HordeSpawner.WorldLoad());
        ServerLifecycleEvents.SERVER_STOPPED.register(new HordeSpawner.ServerStopped());
        ServerTickEvents.END_LEVEL_TICK.register(new HordeSpawner.OnWorldTick());

        ServerLifecycleEvents.SERVER_STARTED.register(new ServerEvents.DeleteOldConfigs());
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> Chronicle.syncAll(handler.player));

        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> CommonEvents.onEntityLoad(entity));
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> CommonEvents.onDeath(entity));
        UseItemCallback.EVENT.register((player, world, hand) -> {
            CommonEvents.onUseItem(player, hand);
            return InteractionResult.PASS;
        });
        UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
            CommonEvents.onUseBlock(player, world, hit.getBlockPos());
            return CommonEvents.stripSmolderingPillar(player, world, hand, hit);
        });
    }
}
