package com.finallion.graveyard.item;

import com.finallion.graveyard.TheGraveyard;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;

public class VialOfBlood extends Item {
    private static float blood;

    public VialOfBlood() {
        super(new FabricItemSettings().maxCount(1).group(TheGraveyard.GROUP));
        blood = 0.0F;
    }

    public void fill() {
        if (blood <= 8) {
            blood++;
        }
    }

    public static float getBlood() {
        return blood;
    }

}
