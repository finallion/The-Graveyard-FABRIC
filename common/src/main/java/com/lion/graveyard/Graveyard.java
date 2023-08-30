package com.lion.graveyard;

import com.lion.graveyard.config.GraveyardConfig;
import com.lion.graveyard.init.*;
import com.lion.graveyard.util.NBTParser;
import draylar.omegaconfig.OmegaConfig;
import net.fabricmc.loader.api.FabricLoader;
import software.bernie.geckolib.GeckoLib;

import java.io.IOException;

public class Graveyard {

    public static final String MOD_ID = "graveyard";
    public static final GraveyardConfig CONFIG = OmegaConfig.register(GraveyardConfig.class);

    public static GraveyardConfig getConfig() {
        return CONFIG;
    }

    public static void init() {
        GeckoLib.initialize();

        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            try {
                NBTParser.parseNBTFiles();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        TGAdvancements.init();
        TGBlocks.init();
        TGEntities.init();
        TGItems.init();
        TGItemGroups.init();
        TGParticles.init();
        TGProcessors.init();
        TGScreens.init();
        TGSounds.init();
        TGStructures.init();
        TGRecipeTypes.init();
        TGTrunkPlacer.init();
        TGFeatures.init();

    }

    public static void postInit() {
        TGBlocks.postInit();
        TGBlockEntities.postInit();
        TGEntities.postInit();
        TGItems.addItemsToGroup();
    }

    public static String createStringID(String name) {
        return MOD_ID + ":" + name;
    }

    /*
    // TODO: templates
    // TODO: trades
    // TODO: sherds
    // TODO: double deco
     */
}
