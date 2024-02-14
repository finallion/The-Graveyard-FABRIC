package com.lion.graveyard.platform.neoforge;


import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class ConfigDirectoryImpl {

    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
}
