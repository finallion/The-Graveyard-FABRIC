package com.lion.graveyard.neoforge;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.GraveyardClient;
import com.lion.graveyard.init.TGEntities;
import com.lion.graveyard.network.ClientPayloadHandler;
import com.lion.graveyard.network.SkullEntitySpawnPacket;
import com.lion.graveyard.platform.neoforge.NamelessHangedTradeOfferResourceListener;
import com.lion.graveyard.platform.neoforge.RegistryHelperImpl;
import com.lion.graveyard.util.SpawnHordeCommand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.SpawnPlacementRegisterEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import software.bernie.geckolib.GeckoLib;

import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings({"deprecation", "removal"})
@Mod(Graveyard.MOD_ID)
public class GraveyardNeoforge {

    public GraveyardNeoforge() {
        IEventBus bus = net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext.get().getModEventBus();

        Graveyard.init();

        if (FMLEnvironment.dist == Dist.CLIENT) {
            GraveyardClient.init();
        }

        RegistryHelperImpl.SOUNDS.register(bus);
        RegistryHelperImpl.BLOCKS.register(bus);
        RegistryHelperImpl.ITEMS.register(bus);
        RegistryHelperImpl.TILE_ENTITIES.register(bus);
        RegistryHelperImpl.ENTITIES.register(bus);
        RegistryHelperImpl.PARTICLES.register(bus);
        RegistryHelperImpl.STRUCTURE_TYPES.register(bus);
        RegistryHelperImpl.STRUCTURE_PROCESSOR_TYPES.register(bus);
        RegistryHelperImpl.FEATURES.register(bus);
        RegistryHelperImpl.TRUNK_PLACERS.register(bus);
        RegistryHelperImpl.RECIPE_TYPES.register(bus);
        RegistryHelperImpl.RECIPE_SERIALIZERS.register(bus);
        RegistryHelperImpl.CREATIVE_TABS.register(bus);
        RegistryHelperImpl.MENUS.register(bus);
        RegistryHelperImpl.CRITERIA.register(bus);

        bus.addListener(GraveyardNeoforge::init);
        bus.addListener(GraveyardNeoforge::registerEntityAttributes);
        bus.addListener(GraveyardNeoforge::addItemsToTabs);
        bus.addListener(GraveyardNeoforge::registerPayloads);

        IEventBus forgeBus = NeoForge.EVENT_BUS;
        forgeBus.addListener(GraveyardNeoforge::registerCommands);
        forgeBus.addListener(GraveyardNeoforge::registerResourceReloader);
    }

    private static void init(final FMLCommonSetupEvent event) {
        event.enqueueWork(Graveyard::postInit);
    }

    @SubscribeEvent
    public static void registerResourceReloader(AddReloadListenerEvent event) {
        event.addListener(new NamelessHangedTradeOfferResourceListener());
    }

    @SubscribeEvent
    public static void registerSpawnPlacements(SpawnPlacementRegisterEvent event) {
        /*
        event.register(TGEntities.REAPER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        event.register(TGEntities.ACOLYTE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkAnyLightMonsterSpawnRules);
        event.register(TGEntities.CORRUPTED_VINDICATOR.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkAnyLightMonsterSpawnRules);
        event.register(TGEntities.CORRUPTED_PILLAGER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkAnyLightMonsterSpawnRules);
        event.register(TGEntities.SKELETON_CREEPER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        event.register(TGEntities.FALLING_CORPSE.get(), SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkAnyLightMonsterSpawnRules);
        event.register(TGEntities.GHOUL.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        event.register(TGEntities.REVENANT.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        event.register(TGEntities.WRAITH.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        event.register(TGEntities.NIGHTMARE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        event.register(TGEntities.LICH.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);

         */
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        SpawnHordeCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        for (Map.Entry<Supplier<? extends EntityType<? extends LivingEntity>>, Supplier<AttributeSupplier.Builder>> entry : RegistryHelperImpl.ENTITY_ATTRIBUTES.entrySet()) {
            event.put(entry.getKey().get(), entry.getValue().get().build());
        }
    }

    @SubscribeEvent
    public static void addItemsToTabs(BuildCreativeModeTabContentsEvent event) {
        RegistryHelperImpl.ITEMS_TO_ADD.forEach((itemGroup, itemPairs) -> {
            if (event.getTabKey() == itemGroup) {
                itemPairs.forEach(item -> {
                    event.getEntries().put(item.getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                });
            }
        });
    }

    @SubscribeEvent
    public static void registerPayloads(final RegisterPayloadHandlerEvent event) {
        final IPayloadRegistrar registrar = event.registrar(Graveyard.MOD_ID);
        registrar.play(GraveyardClient.SKULL_PACKET_ID, SkullEntitySpawnPacket::new, handler -> handler
                .client(ClientPayloadHandler.getInstance()::handleData));
    }
}
