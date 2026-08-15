package com.lion.graveyard.platform.fabric;

import com.lion.graveyard.item.CarvedGravestoneItem;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

public class ItemFactoryImpl {

    /// Fabric needs no subclass: the renderer is registered against the item in the client
    /// entrypoint.
    public static CarvedGravestoneItem carvedGravestone(Block block, Item.Settings settings) {
        return new CarvedGravestoneItem(block, settings);
    }
}
