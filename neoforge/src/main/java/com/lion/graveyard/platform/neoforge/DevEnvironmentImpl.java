package com.lion.graveyard.platform.neoforge;

import net.neoforged.fml.loading.FMLLoader;

public class DevEnvironmentImpl {

    public static boolean isDevelopment() {
        return !FMLLoader.isProduction();
    }
}
