package com.lion.graveyard.forge;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.GraveyardClient;
import com.lion.graveyard.init.TGEntities;
import com.lion.graveyard.init.TGProcessors;
import com.lion.graveyard.network.PacketHandler;
import com.lion.graveyard.platform.forge.NamelessHangedTradeOfferResourceListener;
import com.lion.graveyard.platform.forge.RegistryHelperImpl;
import com.lion.graveyard.platform.forge.ServerEvents;
import com.lion.graveyard.util.SpawnHordeCommand;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.Map;
import java.util.function.Supplier;

@Mod(Graveyard.MOD_ID)
public class GraveyardForge {

    public GraveyardForge() {
        Graveyard.init();
        PacketHandler.registerMessages();

        if (FMLEnvironment.dist == Dist.CLIENT) {
            GraveyardClient.init();
        }

        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        RegistryHelperImpl.SOUNDS.register(bus);
        RegistryHelperImpl.BLOCKS.register(bus);
        RegistryHelperImpl.ITEMS.register(bus);
        RegistryHelperImpl.TILE_ENTITIES.register(bus);
        RegistryHelperImpl.ENTITIES.register(bus);
        RegistryHelperImpl.PARTICLES.register(bus);
        RegistryHelperImpl.STRUCTURE_TYPES.register(bus);
        RegistryHelperImpl.FEATURES.register(bus);
        RegistryHelperImpl.TRUNK_PLACERS.register(bus);
        RegistryHelperImpl.RECIPE_TYPES.register(bus);
        RegistryHelperImpl.RECIPE_SERIALIZERS.register(bus);
        RegistryHelperImpl.CREATIVE_TABS.register(bus);
        RegistryHelperImpl.MENUS.register(bus);

        bus.addListener(GraveyardForge::init);
        bus.addListener(GraveyardForge::registerEntityAttributes);
        bus.addListener(GraveyardForge::addItemsToTabs);

        forgeBus.register(this);
        forgeBus.addListener(GraveyardForge::registerCommands);
        forgeBus.addListener(GraveyardForge::registerResourceReloader);
    }

    private static void init(final FMLCommonSetupEvent event) {
        Registry.register(Registries.STRUCTURE_PROCESSOR, new Identifier(Graveyard.MOD_ID, "remove_waterlogged_processor"), TGProcessors.REMOVE_WATERLOGGED);
        Registry.register(Registries.STRUCTURE_PROCESSOR, new Identifier(Graveyard.MOD_ID, "switch_spawner_processor"), TGProcessors.SWITCH_SPAWNER);
        Registry.register(Registries.STRUCTURE_PROCESSOR, new Identifier(Graveyard.MOD_ID, "waterlogged_crypt_processor"), TGProcessors.REMOVE_WATERLOGGED_CRYPT);

        event.enqueueWork(Graveyard::postInit);
    }

    @SubscribeEvent
    public static void registerResourceReloader(AddReloadListenerEvent event) {
        event.addListener(new NamelessHangedTradeOfferResourceListener());
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        SpawnHordeCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        for (Map.Entry<Supplier<? extends EntityType<? extends LivingEntity>>, Supplier<DefaultAttributeContainer.Builder>> entry : RegistryHelperImpl.ENTITY_ATTRIBUTES.entrySet()) {
            event.put(entry.getKey().get(), entry.getValue().get().build());
        }
    }

    @SubscribeEvent
    public static void addItemsToTabs(BuildCreativeModeTabContentsEvent event) {
        RegistryHelperImpl.ITEMS_TO_ADD.forEach((itemGroup, itemPairs) -> {
            if (event.getTabKey() == itemGroup) {
                itemPairs.forEach(item -> {
                    event.getEntries().put(item.getDefaultStack(), ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS);
                });
            }
        });
    }
}
