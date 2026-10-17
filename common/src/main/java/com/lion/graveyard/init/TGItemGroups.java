package com.lion.graveyard.init;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.chronicle.Chronicle;
import com.lion.graveyard.item.DiaryPageItem;
import com.lion.graveyard.platform.RegistryHelper;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public class TGItemGroups {

    public static final RegistryKey<ItemGroup> ITEM_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP, Graveyard.id("group"));
    public static final RegistryKey<ItemGroup> CHRONICLE_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP, Graveyard.id("chronicle"));

    public static void init() {
        RegistryHelper.registerItemGroup(ITEM_GROUP, "group", "The Graveyard", Items.SKELETON_SKULL);
        RegistryHelper.registerItemGroupWithIcon(CHRONICLE_GROUP, "chronicle", "The Graveyard - Chronicle",
                () -> new ItemStack(TGItems.DIARY_PAGE.get()));
        RegistryHelper.addStacksToItemGroup(CHRONICLE_GROUP, () -> Chronicle.allPages().stream()
                .map(page -> DiaryPageItem.of(TGItems.DIARY_PAGE.get(), page))
                .toList());
    }
}
