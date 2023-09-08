package com.lion.graveyard;

import com.lion.graveyard.config.GraveyardConfig;
import com.lion.graveyard.config.OmegaConfig;
import com.lion.graveyard.init.*;
import com.lion.graveyard.trades.TradeOfferManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib.GeckoLib;


public class Graveyard {

    public static final String MOD_ID = "graveyard";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final GraveyardConfig CONFIG = OmegaConfig.register(GraveyardConfig.class);

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
        TGTrunkPlacer.init();
        TGFeatures.init();

        TradeOfferManager.registerTradeOffers();
    }

    public static void postInit() {
        TGBlocks.postInit();
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
