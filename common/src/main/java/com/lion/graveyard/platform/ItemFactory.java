package com.lion.graveyard.platform;

import com.lion.graveyard.item.CarvedGravestoneItem;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

public class ItemFactory {

    /// The carved gravestone draws itself, because its texture only exists at runtime. Fabric
    /// registers such a renderer from the client entrypoint, Forge only through an override on the
    /// item class itself - so the item has to be built per loader.
    @ExpectPlatform
    public static CarvedGravestoneItem carvedGravestone(Block block, Item.Settings settings) {
        throw new AssertionError();
    }
}
