package com.lion.graveyard.platform.neoforge;

import net.neoforged.fml.loading.FMLEnvironment;

public class DevEnvironmentImpl {

    public static boolean isDevelopment() {
        return !FMLEnvironment.isProduction();
    }
}
