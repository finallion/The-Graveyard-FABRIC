package com.lion.graveyard;

import com.lion.graveyard.config.GraveyardConfig;
import com.lion.graveyard.config.OmegaConfig;
import com.lion.graveyard.init.*;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib.GeckoLib;

public class Graveyard {

    public static final String MOD_ID = "graveyard";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final GraveyardConfig CONFIG = OmegaConfig.register(GraveyardConfig.class);

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static GraveyardConfig getConfig() {
        return CONFIG;
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public static void init() {
        GeckoLib.initialize();

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
