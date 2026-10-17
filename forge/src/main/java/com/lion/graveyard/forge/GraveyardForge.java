package com.lion.graveyard.forge;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.GraveyardClient;
import com.lion.graveyard.init.TGOverrunVillage;
import com.lion.graveyard.init.TGProcessors;
import com.lion.graveyard.network.PacketHandler;
import com.lion.graveyard.platform.forge.NamelessHangedTradeOfferResourceListener;
import com.lion.graveyard.platform.forge.RegistryHelperImpl;
import com.lion.graveyard.util.SpawnHordeCommand;
import com.lion.graveyard.util.ChronicleCommand;
import com.lion.graveyard.chronicle.Chronicle;
import com.lion.graveyard.event.CommonEvents;
import net.minecraft.block.BlockState;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.common.world.BiomeModifier;
import com.mojang.serialization.Codec;

import java.util.Map;
import java.util.function.Supplier;

@Mod(Graveyard.MOD_ID)
public class GraveyardForge {
    private static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, Graveyard.MOD_ID);

    static {
        BIOME_MODIFIERS.register("config_spawns", () -> ConfigSpawnsBiomeModifier.CODEC);
    }

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
        RegistryHelperImpl.LOOT_CONDITIONS.register(bus);
        RegistryHelperImpl.RECIPE_TYPES.register(bus);
        RegistryHelperImpl.RECIPE_SERIALIZERS.register(bus);
        RegistryHelperImpl.CREATIVE_TABS.register(bus);
        RegistryHelperImpl.MENUS.register(bus);
        BIOME_MODIFIERS.register(bus);

        bus.addListener(GraveyardForge::init);
        bus.addListener(GraveyardForge::registerEntityAttributes);
        bus.addListener(GraveyardForge::addItemsToTabs);

        forgeBus.register(this);
        forgeBus.addListener(GraveyardForge::registerCommands);
        forgeBus.addListener((PlayerEvent.PlayerLoggedInEvent event) -> {
            if (event.getEntity() instanceof ServerPlayerEntity player) {
                Chronicle.syncAll(player);
            }
        });
        forgeBus.addListener(GraveyardForge::registerResourceReloader);
        forgeBus.addListener((EntityJoinLevelEvent event) -> CommonEvents.onEntityLoad(event.getEntity()));
        forgeBus.addListener(EventPriority.LOWEST, (LivingDeathEvent event) -> CommonEvents.onDeath(event.getEntity()));
        forgeBus.addListener(EventPriority.LOWEST, (PlayerInteractEvent.RightClickItem event) -> CommonEvents.onUseItem(event.getEntity(), event.getHand()));
        forgeBus.addListener(EventPriority.LOWEST, GraveyardForge::onRightClickBlock);
        forgeBus.addListener(GraveyardForge::onToolModification);
    }

    private static void init(final FMLCommonSetupEvent event) {
        Registry.register(Registries.STRUCTURE_PROCESSOR, Graveyard.id("remove_waterlogged_processor"), TGProcessors.REMOVE_WATERLOGGED);
        Registry.register(Registries.STRUCTURE_PROCESSOR, Graveyard.id("switch_spawner_processor"), TGProcessors.SWITCH_SPAWNER);
        Registry.register(Registries.STRUCTURE_PROCESSOR, Graveyard.id("waterlogged_crypt_processor"), TGProcessors.REMOVE_WATERLOGGED_CRYPT);
        Registry.register(Registries.STRUCTURE_PROCESSOR, Graveyard.id("ruin"), TGOverrunVillage.RUIN);
        Registry.register(Registries.STRUCTURE_PROCESSOR, Graveyard.id("pool_aliases"), TGOverrunVillage.POOL_ALIASES);

        event.enqueueWork(Graveyard::postInit);
    }

    private static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getUseBlock() != Event.Result.DENY) {
            CommonEvents.onUseBlock(event.getEntity(), event.getLevel(), event.getPos());
        }
    }

    private static void onToolModification(BlockEvent.BlockToolModificationEvent event) {
        if (event.getToolAction() == ToolActions.AXE_STRIP) {
            BlockState stripped = CommonEvents.smolderingStrippedState(event.getState());

            if (stripped != null) {
                event.setFinalState(stripped);
            }
        }
    }

    @SubscribeEvent
    public static void registerResourceReloader(AddReloadListenerEvent event) {
        event.addListener(new NamelessHangedTradeOfferResourceListener());
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        SpawnHordeCommand.register(event.getDispatcher());
        ChronicleCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        for (Map.Entry<Supplier<? extends EntityType<? extends LivingEntity>>, Supplier<DefaultAttributeContainer.Builder>> entry : RegistryHelperImpl.ENTITY_ATTRIBUTES.entrySet()) {
            event.put(entry.getKey().get(),
                    entry.getValue().get().build());
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
        RegistryHelperImpl.STACKS_TO_ADD.forEach((itemGroup, suppliers) -> {
            if (event.getTabKey() == itemGroup) {
                suppliers.forEach(supplier -> supplier.get().forEach(stack ->
                        event.getEntries().put(stack, ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS)));
            }
        });
    }
}
