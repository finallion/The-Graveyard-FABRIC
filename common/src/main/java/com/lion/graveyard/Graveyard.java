package com.lion.graveyard;

import com.lion.graveyard.config.ConfigFiles;
import com.lion.graveyard.config.GraveyardConfig;
import com.lion.graveyard.config.Slf4jConfigLog;
import com.lion.graveyard.init.*;
import com.lion.graveyard.platform.ConfigDirectory;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Graveyard {

    public static final String MOD_ID = "graveyard";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final GraveyardConfig CONFIG = new GraveyardConfig(ConfigFiles.load(ConfigDirectory.getConfigDirectory().resolve("graveyard"), new Slf4jConfigLog(LOGGER)));

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    public static GraveyardConfig getConfig() {
        return CONFIG;
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public static void init() {
        TGAdvancements.init();
        TGItems.init();
        TGBlocks.init();
        TGEntities.init();
        TGItemGroups.init();
        TGBlockEntities.init();
        TGParticles.init();
        TGProcessors.init();
        TGScreens.init();
        TGSounds.init();
        TGStructures.init();
        TGRecipeTypes.init();
        TGLootConditions.init();
        TGOverrunVillage.init();
    }

    public static void postInit() {
        TGBlocks.postInit();
        TGEntities.postInit();
        TGItems.addItemsToGroup();
        TGOverrunVillage.addItemsToGroup();
    }

    public static String createStringID(String name) {
        return MOD_ID + ":" + name;
    }
}
