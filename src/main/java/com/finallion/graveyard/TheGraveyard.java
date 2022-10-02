package com.finallion.graveyard;

import com.finallion.graveyard.config.GraveyardConfig;
import com.finallion.graveyard.entities.*;
import com.finallion.graveyard.init.*;


import com.finallion.graveyard.util.BiomeModification;;
import com.finallion.graveyard.util.MobSpawningRules;
import com.finallion.graveyard.util.TGCommands;
import com.finallion.graveyard.util.TGTags;
import draylar.omegaconfig.OmegaConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.GeckoLib;

public class TheGraveyard implements ModInitializer {
    public static final String MOD_ID = "graveyard";
    public static final GraveyardConfig config = OmegaConfig.register(GraveyardConfig.class);
    public static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onInitialize() {
        GeckoLib.initialize();

        /* HORDE TICK EVENT */
        if (config.getHorde(new Identifier(MOD_ID, "horde_spawn")).enabled) {
            ServerWorldEvents.LOAD.register(new TGSpawner.WorldLoad());
            ServerLifecycleEvents.SERVER_STOPPED.register(new TGSpawner.ServerStopped());
            ServerTickEvents.END_WORLD_TICK.register(new TGSpawner.OnWorldTick());
        }

        /* GENERAL INIT REGISTRY */
        TGAdvancements.init();
        TGSounds.init();
        TGBlocks.registerBlocks();
        TGItems.registerItems();
        TGEntities.registerEntities();
        TGProcessors.registerProcessors();

        /* FEATURE INIT REGISTRY */
        TGStructureType.init();
        TGTags.init();
        TGStructureSets.init();
        TGConfiguredStructureFeatures.init();

        /* BIOME MODIFICATION */
        MobSpawningRules.addSpawnEntries();

        /* COMPAT */
        if (FabricLoader.getInstance().isModLoaded("graveyard_biomes")) {
            BiomeModification.init();
        }

        /* ENTITY ATTRIBUTES */
        FabricDefaultAttributeRegistry.register(TGEntities.LICH, LichEntity.createLichAttributes());
        FabricDefaultAttributeRegistry.register(TGEntities.FALLING_CORPSE, FallingCorpse.createFallingCorpseAttributes());
        FabricDefaultAttributeRegistry.register(TGEntities.REAPER, ReaperEntity.createReaperAttributes());
        FabricDefaultAttributeRegistry.register(TGEntities.ACOLYTE, CorruptedIllager.createCorruptedIllagerAttributes());
        FabricDefaultAttributeRegistry.register(TGEntities.CORRUPTED_VINDICATOR, CorruptedIllager.createCorruptedIllagerAttributes());
        FabricDefaultAttributeRegistry.register(TGEntities.CORRUPTED_PILLAGER, CorruptedPillager.createCorruptedPillagerAttributes());
        FabricDefaultAttributeRegistry.register(TGEntities.SKELETON_CREEPER, SkeletonCreeper.createSkeletonCreeperAttributes());
        FabricDefaultAttributeRegistry.register(TGEntities.GHOUL, GhoulEntity.createGhoulAttributes());
        FabricDefaultAttributeRegistry.register(TGEntities.REVENANT, RevenantEntity.createRevenantAttributes());
        FabricDefaultAttributeRegistry.register(TGEntities.WRAITH, WraithEntity.createWraithAttributes());
        FabricDefaultAttributeRegistry.register(TGEntities.NIGHTMARE, NightmareEntity.createNightmareAttributes());
        FabricDefaultAttributeRegistry.register(TGEntities.GHOULING, GhoulingEntity.createGhoulingAttributes());

        /* COMMANDS */
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(CommandManager.literal("horde").requires((source) -> {
            return source.hasPermissionLevel(3);
        }).then(CommandManager.literal("trigger").executes((context) -> {
            return TGCommands.executeSpawn((ServerCommandSource) context.getSource());
        }))));

        /* OLD CONFIG DELETE */
        ServerLifecycleEvents.SERVER_STARTED.register(new TGFileWriterReader.ServerStarted());

        /* GHOULING UUID FILE SAVE AND READ */
        ServerWorldEvents.LOAD.register(new TGFileWriterReader.Load());
        ServerWorldEvents.UNLOAD.register(new TGFileWriterReader.Unload());
    }

    public static ItemGroup GROUP = FabricItemGroupBuilder.create(
                    new Identifier(MOD_ID, "group"))
            .icon(() -> new ItemStack(Items.SKELETON_SKULL)).build();
}
