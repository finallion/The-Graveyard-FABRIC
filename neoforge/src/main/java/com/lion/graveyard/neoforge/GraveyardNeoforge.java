package com.lion.graveyard.neoforge;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.GraveyardClient;
import com.lion.graveyard.chronicle.Chronicle;
import com.lion.graveyard.entities.MobSpawnGate;
import com.lion.graveyard.event.CommonEvents;
import com.lion.graveyard.network.PacketHandler;
import com.lion.graveyard.platform.neoforge.NamelessHangedTradeOfferResourceListener;
import com.lion.graveyard.platform.neoforge.RegistryHelperImpl;
import com.lion.graveyard.util.ChronicleCommand;
import com.lion.graveyard.util.SpawnHordeCommand;
import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.TriState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Map;
import java.util.function.Supplier;

@Mod(Graveyard.MOD_ID)
public class GraveyardNeoforge {
    private static final DeferredRegister<MapCodec<? extends BiomeModifier>> BIOME_MODIFIERS = DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, Graveyard.MOD_ID);

    static {
        BIOME_MODIFIERS.register("config_spawns", () -> ConfigSpawnsBiomeModifier.CODEC);
    }

    public GraveyardNeoforge(IEventBus modBus) {
        Graveyard.init();

        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            GraveyardClient.init();
        }

        RegistryHelperImpl.SOUNDS.register(modBus);
        RegistryHelperImpl.BLOCKS.register(modBus);
        RegistryHelperImpl.ITEMS.register(modBus);
        RegistryHelperImpl.TILE_ENTITIES.register(modBus);
        RegistryHelperImpl.ENTITIES.register(modBus);
        RegistryHelperImpl.PARTICLES.register(modBus);
        RegistryHelperImpl.STRUCTURE_TYPES.register(modBus);
        RegistryHelperImpl.LOOT_CONDITIONS.register(modBus);
        RegistryHelperImpl.RECIPE_TYPES.register(modBus);
        RegistryHelperImpl.RECIPE_SERIALIZERS.register(modBus);
        RegistryHelperImpl.CREATIVE_TABS.register(modBus);
        RegistryHelperImpl.MENUS.register(modBus);
        RegistryHelperImpl.STRUCTURE_PROCESSORS.register(modBus);
        RegistryHelperImpl.TRIGGERS.register(modBus);
        RegistryHelperImpl.DATA_COMPONENTS.register(modBus);
        BIOME_MODIFIERS.register(modBus);

        modBus.addListener(GraveyardNeoforge::init);
        modBus.addListener(GraveyardNeoforge::registerEntityAttributes);
        modBus.addListener(GraveyardNeoforge::addItemsToTabs);
        modBus.addListener(GraveyardNeoforge::registerSpawnPlacements);
        modBus.addListener(PacketHandler::registerPayloads);

        NeoForge.EVENT_BUS.addListener(GraveyardNeoforge::registerCommands);
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent event) -> {
            if (event.getEntity() instanceof ServerPlayer player) {
                Chronicle.syncAll(player);
            }
        });
        NeoForge.EVENT_BUS.addListener(GraveyardNeoforge::registerResourceReloader);
        NeoForge.EVENT_BUS.addListener((EntityJoinLevelEvent event) -> CommonEvents.onEntityLoad(event.getEntity()));
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, (LivingDeathEvent event) -> CommonEvents.onDeath(event.getEntity()));
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, (PlayerInteractEvent.RightClickItem event) -> CommonEvents.onUseItem(event.getEntity(), event.getHand()));
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, GraveyardNeoforge::onRightClickBlock);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, GraveyardNeoforge::gatePositionCheck);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, GraveyardNeoforge::gateSpawnPlacement);
        NeoForge.EVENT_BUS.addListener(GraveyardNeoforge::onToolModification);
    }

    private static void init(final FMLCommonSetupEvent event) {
        event.enqueueWork(Graveyard::postInit);
    }

    private static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getUseBlock() != TriState.FALSE) {
            CommonEvents.onUseBlock(event.getEntity(), event.getLevel(), event.getPos());
        }
    }

    private static void gatePositionCheck(MobSpawnEvent.PositionCheck event) {
        Mob mob = event.getEntity();

        if (MobSpawnGate.blocks(mob.getType(), event.getSpawnType())
                || MobSpawnGate.inForbiddenBiome(event.getLevel(), mob.blockPosition(), mob.getType(), event.getSpawnType())) {
            event.setResult(MobSpawnEvent.PositionCheck.Result.FAIL);
        }
    }

    private static void gateSpawnPlacement(MobSpawnEvent.SpawnPlacementCheck event) {
        if (MobSpawnGate.blocks(event.getEntityType(), event.getSpawnType())
                || MobSpawnGate.inForbiddenBiome(event.getLevel(), event.getPos(), event.getEntityType(), event.getSpawnType())) {
            event.setResult(MobSpawnEvent.SpawnPlacementCheck.Result.FAIL);
        }
    }

    private static void onToolModification(BlockEvent.BlockToolModificationEvent event) {
        if (event.getItemAbility() == ItemAbilities.AXE_STRIP) {
            BlockState stripped = CommonEvents.smolderingStrippedState(event.getState());

            if (stripped != null) {
                event.setFinalState(stripped);
            }
        }
    }

    private static void registerResourceReloader(AddServerReloadListenersEvent event) {
        event.addListener(Graveyard.id("nameless_hanged_trades_data_loader"), new NamelessHangedTradeOfferResourceListener());
    }

    private static void registerCommands(RegisterCommandsEvent event) {
        SpawnHordeCommand.register(event.getDispatcher());
        ChronicleCommand.register(event.getDispatcher());
    }

    private static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        for (Map.Entry<Supplier<? extends EntityType<? extends LivingEntity>>, Supplier<AttributeSupplier.Builder>> entry : RegistryHelperImpl.ENTITY_ATTRIBUTES.entrySet()) {
            event.put(entry.getKey().get(), entry.getValue().get().build());
        }
    }

    private static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        RegistryHelperImpl.SPAWN_PLACEMENTS.forEach(registration -> registration.accept(event));
    }

    private static void addItemsToTabs(BuildCreativeModeTabContentsEvent event) {
        RegistryHelperImpl.ITEMS_TO_ADD.forEach((itemGroup, itemPairs) -> {
            if (event.getTabKey() == itemGroup) {
                itemPairs.forEach(item -> {
                    event.accept(item.getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                });
            }
        });
        RegistryHelperImpl.STACKS_TO_ADD.forEach((itemGroup, suppliers) -> {
            if (event.getTabKey() == itemGroup) {
                suppliers.forEach(supplier -> supplier.get().forEach(stack ->
                        event.accept(stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS)));
            }
        });
    }
}
