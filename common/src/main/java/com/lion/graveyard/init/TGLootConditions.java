package com.lion.graveyard.init;

import com.lion.graveyard.loot.PagesInLootCondition;
import com.lion.graveyard.platform.RegistryHelper;

/// Loot conditions of the mod. They have to exist before the first data pack is read, so this runs
/// with the rest of the registration and not on world load.
public final class TGLootConditions {

    private TGLootConditions() {}

    public static void init() {
        RegistryHelper.registerLootCondition("pages_in_loot", PagesInLootCondition.MAP_CODEC);
    }
}
