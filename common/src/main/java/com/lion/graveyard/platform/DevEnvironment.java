package com.lion.graveyard.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class DevEnvironment {

    @ExpectPlatform
    public static boolean isDevelopment() {
        throw new AssertionError();
    }
}
