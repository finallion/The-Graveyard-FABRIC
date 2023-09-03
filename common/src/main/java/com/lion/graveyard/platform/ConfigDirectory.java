package com.lion.graveyard.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;

import java.nio.file.Path;

public class ConfigDirectory {

    @ExpectPlatform
    public static Path getConfigDirectory() {
        throw new AssertionError();
    }
}
