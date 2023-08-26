package com.lion.graveyard.fabric;

import com.lion.graveyard.Graveyard;
import net.fabricmc.api.ModInitializer;

public class GraveyardFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Graveyard.init();


    }
}
