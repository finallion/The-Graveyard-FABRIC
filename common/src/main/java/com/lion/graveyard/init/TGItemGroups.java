package com.lion.graveyard.init;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.platform.RegistryHelper;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class TGItemGroups {

    public static final RegistryKey<ItemGroup> ITEM_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier(Graveyard.MOD_ID, "group"));

    public static void init() {
        RegistryHelper.registerItemGroup(ITEM_GROUP, "group", "The Graveyard", Items.SKELETON_SKULL);
    }
}
