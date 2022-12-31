package com.finallion.graveyard.config;

import java.util.List;

public class StructureConfigEntry {
    public final boolean enabled;

    private StructureConfigEntry(boolean enabled) {
        this.enabled = enabled;
    }

    public static StructureConfigEntry of() {
        return new StructureConfigEntry(true);
    }

}
