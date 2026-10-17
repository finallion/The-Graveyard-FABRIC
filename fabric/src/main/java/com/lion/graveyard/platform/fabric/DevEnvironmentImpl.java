package com.lion.graveyard.platform.fabric;

import net.fabricmc.loader.api.FabricLoader;

public class DevEnvironmentImpl {

    public static boolean isDevelopment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }
}
