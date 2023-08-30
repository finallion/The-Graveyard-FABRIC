package com.lion.graveyard.init;

import com.lion.graveyard.gui.OssuaryScreenHandler;
import com.lion.graveyard.platform.RegistryHelper;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;

public class TGScreens {

    public static void init() {}

    public static final ScreenHandlerType<OssuaryScreenHandler> OSSUARY_SCREEN_HANDLER = new ScreenHandlerType<>(OssuaryScreenHandler::new, FeatureFlags.VANILLA_FEATURES);

    static {
        RegistryHelper.registerScreenHandlerType("ossuary_screen_handler", OSSUARY_SCREEN_HANDLER);
    }

}
