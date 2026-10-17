package com.lion.graveyard.platform.forge;

import net.minecraftforge.fml.loading.FMLLoader;

public class DevEnvironmentImpl {

    public static boolean isDevelopment() {
        return !FMLLoader.isProduction();
    }
}
