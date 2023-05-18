package com.finallion.graveyard.init;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.client.gui.OssuaryScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class TGScreens {

    public static void init() {}

    public final static ScreenHandlerType<OssuaryScreenHandler> OSSUARY_SCREEN_HANDLER =
            Registry.register(Registry.SCREEN_HANDLER, new Identifier(TheGraveyard.MOD_ID, "ossuary_screen_handler"), new ScreenHandlerType<>(OssuaryScreenHandler::new));

}
