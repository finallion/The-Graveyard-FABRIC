package com.lion.graveyard.init;

import com.lion.graveyard.gui.OssuaryScreenHandler;
import com.lion.graveyard.platform.RegistryHelper;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public class TGScreens {

    public static void init() {}

    public static final MenuType<OssuaryScreenHandler> OSSUARY_SCREEN_HANDLER = new MenuType<>(OssuaryScreenHandler::new, FeatureFlags.VANILLA_SET);

    static {
        RegistryHelper.registerScreenHandlerType("ossuary_screen_handler", OSSUARY_SCREEN_HANDLER);
    }

}
