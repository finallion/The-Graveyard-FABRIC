package com.finallion.graveyard.config;

public class ConfigBooleanEntry {
    public final boolean canGenerate;


    private ConfigBooleanEntry(boolean canGenerate) {
        this.canGenerate = canGenerate;
    }

    private ConfigBooleanEntry() {
        this(true);
    }

    public static ConfigBooleanEntry of() {
        return new ConfigBooleanEntry();
    }
}
