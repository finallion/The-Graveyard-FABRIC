package com.finallion.graveyard;

import com.finallion.graveyard.config.GraveyardConfig;
import com.finallion.graveyard.entities.*;
import com.finallion.graveyard.init.*;


import com.finallion.graveyard.recipe.TGRecipeTypes;
import com.finallion.graveyard.util.*;;
import draylar.omegaconfig.OmegaConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib.GeckoLib;


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
        TGRecipeTypes.init();
        TGScreens.init();
        TGParticles.init();
        TGAdvancements.init();
        TGSounds.init();
        TGItems.registerItems();
        TGBlocks.registerBlocks();
        TGEntities.registerEntities();
        TGProcessors.registerProcessors();

        /* FEATURE INIT REGISTRY */
        TGStructureType.init();
        //TGStructureSets.init();
        TGConfiguredStructureFeatures.registerStructureFeatures();

        /* BIOME MODIFICATION */
        MobSpawningRules.addSpawnEntries();


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
        FabricDefaultAttributeRegistry.register(TGEntities.NAMELESS_HANGED, NamelessHangedEntity.createNamelessHangedAttributes());

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


    public static ItemGroup GROUP = FabricItemGroup.builder(new Identifier(MOD_ID, "group"))
            .displayName(Text.literal("The Graveyard"))
            .icon(() -> new ItemStack(Items.SKELETON_SKULL))
            .entries((enabledFeatures, entries, operatorEnabled) -> {
                entries.add(TGItems.CORRUPTION);
                entries.add(TGItems.DARK_IRON_BLOCK);
                entries.add(TGItems.DARK_IRON_INGOT);
                entries.add(TGItems.DARK_IRON_DOOR);
                entries.add(TGItems.DARK_IRON_TRAPDOOR);
                entries.add(TGItems.DARK_IRON_BARS);
                entries.add(TGItems.SOUL_FIRE_BRAZIER);
                entries.add(TGItems.FIRE_BRAZIER);
                entries.add(TGItems.PEDESTAL);
                entries.add(TGItems.CANDLE_HOLDER);
                entries.add(TGItems.GRAVESTONE);
                entries.add(TGItems.COBBLESTONE_GRAVESTONE);
                entries.add(TGItems.MOSSY_COBBLESTONE_GRAVESTONE);
                entries.add(TGItems.DEEPSLATE_GRAVESTONE);
                entries.add(TGItems.BLACKSTONE_GRAVESTONE);
                entries.add(TGItems.GILDED_BLACKSTONE_GRAVESTONE);
                entries.add(TGItems.CRACKED_BLACKSTONE_GRAVESTONE);
                entries.add(TGItems.STONE_BRICKS_GRAVESTONE);
                entries.add(TGItems.MOSSY_STONE_BRICKS_GRAVESTONE);
                entries.add(TGItems.BRICKS_GRAVESTONE);
                entries.add(TGItems.QUARTZ_BRICKS_GRAVESTONE);
                entries.add(TGItems.RED_SANDSTONE_GRAVESTONE);
                entries.add(TGItems.SANDSTONE_GRAVESTONE);

                entries.add(TGItems.SKULL_WITH_RIB_CAGE);
                entries.add(TGItems.LEANING_SKELETON);
                entries.add(TGItems.SKULL_PILE);
                entries.add(TGItems.LYING_SKELETON);
                entries.add(TGItems.WITHER_SKULL_WITH_RIB_CAGE);
                entries.add(TGItems.LEANING_WITHER_SKELETON);
                entries.add(TGItems.WITHER_SKULL_PILE);
                entries.add(TGItems.LYING_WITHER_SKELETON);
                entries.add(TGItems.CREEPER_SKELETON);
                entries.add(TGItems.SKELETON_HAND);
                entries.add(TGItems.WITHER_SKELETON_HAND);
                entries.add(TGItems.TORSO_PILE);
                entries.add(TGItems.WITHER_TORSO_PILE);
                entries.add(TGItems.SKULL_ON_PIKE);
                entries.add(TGItems.WITHER_SKULL_ON_PIKE);
                entries.add(TGItems.BONE_REMAINS);
                entries.add(TGItems.WITHER_BONE_REMAINS);
                entries.add(TGItems.LATERALLY_LYING_SKELETON);
                entries.add(TGItems.LATERALLY_LYING_WITHER_SKELETON);
                entries.add(TGItems.HANGED_SKELETON);
                entries.add(TGItems.HANGED_WITHER_SKELETON);

                entries.add(TGItems.OSSUARY);

                entries.add(TGItems.BLACK_URN);
                entries.add(TGItems.GRAY_URN);
                entries.add(TGItems.LIGHT_GRAY_URN);
                entries.add(TGItems.WHITE_URN);
                entries.add(TGItems.LIGHT_BLUE_URN);
                entries.add(TGItems.BLUE_URN);
                entries.add(TGItems.CYAN_URN);
                entries.add(TGItems.GREEN_URN);
                entries.add(TGItems.LIME_URN);
                entries.add(TGItems.PINK_URN);
                entries.add(TGItems.MAGENTA_URN);
                entries.add(TGItems.PURPLE_URN);
                entries.add(TGItems.RED_URN);
                entries.add(TGItems.ORANGE_URN);
                entries.add(TGItems.YELLOW_URN);
                entries.add(TGItems.BROWN_URN);

                entries.add(TGItems.SMALL_BLACK_URN);
                entries.add(TGItems.SMALL_GRAY_URN);
                entries.add(TGItems.SMALL_LIGHT_GRAY_URN);
                entries.add(TGItems.SMALL_WHITE_URN);
                entries.add(TGItems.SMALL_LIGHT_BLUE_URN);
                entries.add(TGItems.SMALL_BLUE_URN);
                entries.add(TGItems.SMALL_CYAN_URN);
                entries.add(TGItems.SMALL_GREEN_URN);
                entries.add(TGItems.SMALL_LIME_URN);
                entries.add(TGItems.SMALL_PINK_URN);
                entries.add(TGItems.SMALL_MAGENTA_URN);
                entries.add(TGItems.SMALL_PURPLE_URN);
                entries.add(TGItems.SMALL_RED_URN);
                entries.add(TGItems.SMALL_ORANGE_URN);
                entries.add(TGItems.SMALL_YELLOW_URN);
                entries.add(TGItems.SMALL_BROWN_URN);

                entries.add(TGItems.VASE_BLOCK);

                entries.add(TGItems.SARCOPHAGUS);
                entries.add(TGItems.OAK_COFFIN);
                entries.add(TGItems.DARK_OAK_COFFIN);
                entries.add(TGItems.SPRUCE_COFFIN);
                entries.add(TGItems.BIRCH_COFFIN);
                entries.add(TGItems.JUNGLE_COFFIN);
                entries.add(TGItems.ACACIA_COFFIN);
                entries.add(TGItems.WARPED_COFFIN);
                entries.add(TGItems.CRIMSON_COFFIN);
                entries.add(TGItems.MANGROVE_COFFIN);

                entries.add(TGItems.SKELETON_CREEPER_SPAWN_EGG);
                entries.add(TGItems.ACOLYTE_SPAWN_EGG);
                entries.add(TGItems.GHOUL_SPAWN_EGG);
                entries.add(TGItems.REAPER_SPAWN_EGG);
                entries.add(TGItems.REVENANT_SPAWN_EGG);
                entries.add(TGItems.NIGHTMARE_SPAWN_EGG);
                entries.add(TGItems.CORRUPTED_VINDICATOR_SPAWN_EGG);
                entries.add(TGItems.CORRUPTED_PILLAGER_SPAWN_EGG);
                entries.add(TGItems.WRAITH_SPAWN_EGG);
                entries.add(TGItems.LICH_SPAWN_EGG);
                entries.add(TGItems.NAMELESS_HANGED_SPAWN_EGG);

                entries.add(TGItems.BONE_DAGGER);
                entries.add(TGItems.WHITE_BONE_STAFF);
                entries.add(TGItems.BLACK_BONE_STAFF);
                entries.add(TGItems.RED_BONE_STAFF);
                entries.add(TGItems.CYAN_BONE_STAFF);
                entries.add(TGItems.PURPLE_BONE_STAFF);

                entries.add(TGItems.ALTAR);
                entries.add(TGItems.ALTAR_SIDE);
                entries.add(TGItems.ALTAR_CORNER);
                entries.add(TGItems.ALTAR_CENTER);
                entries.add(TGItems.UPPER_BONE_STAFF);
                entries.add(TGItems.MIDDLE_BONE_STAFF);
                entries.add(TGItems.LOWER_BONE_STAFF);
                entries.add(TGItems.VIAL_OF_BLOOD);
            })
            .build();

}
