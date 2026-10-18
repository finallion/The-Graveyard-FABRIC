package com.lion.graveyard.init;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.chronicle.Chronicle;
import com.lion.graveyard.item.DiaryPageItem;
import com.lion.graveyard.platform.RegistryHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class TGItemGroups {

    public static final ResourceKey<CreativeModeTab> ITEM_GROUP = ResourceKey.create(Registries.CREATIVE_MODE_TAB, Graveyard.id("group"));
    public static final ResourceKey<CreativeModeTab> CHRONICLE_GROUP = ResourceKey.create(Registries.CREATIVE_MODE_TAB, Graveyard.id("chronicle"));

    public static void init() {
        RegistryHelper.registerItemGroup(ITEM_GROUP, "group", "The Graveyard", Items.SKELETON_SKULL);
        RegistryHelper.registerItemGroupWithIcon(
                CHRONICLE_GROUP,
                "chronicle",
                "The Graveyard - Chronicle",
                () -> new ItemStack(TGItems.DIARY_PAGE.get())
        );
        RegistryHelper.addStacksToItemGroup(
                CHRONICLE_GROUP,
                () -> Chronicle.allPages().stream()
                        .map(page -> DiaryPageItem.of(TGItems.DIARY_PAGE.get(), page))
                        .toList()
        );
    }
}
