package com.lion.graveyard.forge;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.GraveyardClient;
import com.lion.graveyard.platform.forge.RegistryHelperImpl;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(Graveyard.MOD_ID)
public class GraveyardForge {

    public GraveyardForge() {
        Graveyard.init();

        if (FMLEnvironment.dist == Dist.CLIENT) {
            GraveyardClient.init();
        }

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        RegistryHelperImpl.BLOCKS.register(bus);
        RegistryHelperImpl.TILE_ENTITIES.register(bus);

        bus.addListener(GraveyardForge::init);

    }

    private static void init(final FMLCommonSetupEvent event) {

    }
}
