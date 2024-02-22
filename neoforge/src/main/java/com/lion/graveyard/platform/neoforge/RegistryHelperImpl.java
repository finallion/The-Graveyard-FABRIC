package com.lion.graveyard.platform.neoforge;

import com.lion.graveyard.Graveyard;
import com.mojang.serialization.Codec;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class RegistryHelperImpl {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, Graveyard.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Graveyard.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Graveyard.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, Graveyard.MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, Graveyard.MOD_ID);
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister.create(BuiltInRegistries.STRUCTURE_TYPE, Graveyard.MOD_ID);
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, Graveyard.MOD_ID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, Graveyard.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, Graveyard.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, Graveyard.MOD_ID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, Graveyard.MOD_ID);
    public static final DeferredRegister<TrunkPlacerType<?>> TRUNK_PLACERS = DeferredRegister.create(BuiltInRegistries.TRUNK_PLACER_TYPE, Graveyard.MOD_ID);
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, Graveyard.MOD_ID);
    public static final DeferredRegister<CriterionTrigger<?>> CRITERIA = DeferredRegister.create(BuiltInRegistries.TRIGGER_TYPES, Graveyard.MOD_ID);
    public static final DeferredRegister<StructureProcessorType<?>> STRUCTURE_PROCESSOR_TYPES = DeferredRegister.create(BuiltInRegistries.STRUCTURE_PROCESSOR, Graveyard.MOD_ID);


    public static final Map<ModelLayerLocation, Supplier<LayerDefinition>> ENTITY_MODEL_LAYERS = new ConcurrentHashMap<>();
    public static final Map<Supplier<? extends EntityType<? extends LivingEntity>>, Supplier<AttributeSupplier.Builder>> ENTITY_ATTRIBUTES = new ConcurrentHashMap<>();
    public static final HashMap<ResourceKey<CreativeModeTab>, List<Item>> ITEMS_TO_ADD = new HashMap<>();

    public static <T extends Block> Supplier<T> registerBlock(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }

    public static <T extends BlockEntityType<?>> Supplier<T> registerBlockEntity(String name, Supplier<T> blockEntity) {
        return TILE_ENTITIES.register(name, blockEntity);
    }

    public static void registerItemGroup(ResourceKey<CreativeModeTab> registryKey, String name, String literalName, Item item) {
        CREATIVE_TABS.register(name, () -> new CreativeModeTab.Builder(CreativeModeTab.Row.TOP,0)
                .icon(() -> new ItemStack(item))
                .title(Component.literal(literalName)).build());
    }

    public static <T extends Item> Supplier<T> registerItem(String name, Supplier<T> item) {
        return ITEMS.register(name, item);
    }
    public static void addToItemGroup(ResourceKey<CreativeModeTab> itemGroup, Item item) {
        if (ITEMS_TO_ADD.containsKey(itemGroup)) {
            ITEMS_TO_ADD.get(itemGroup).add(item);
        } else {
            List<Item> items = new ArrayList<>();
            items.add(item);
            ITEMS_TO_ADD.put(itemGroup, items);
        }
    }

    public static <T extends Item> Supplier<T> registerSpawnEggItem(String name, Supplier<? extends EntityType<? extends Mob>> type, int backgroundColor, int highlightColor, Item.Properties props) {
        return (Supplier<T>) registerItem(name, () -> new DeferredSpawnEggItem(type, backgroundColor, highlightColor, props));
    }

    public static <T extends Item> Supplier<T> registerMusicDiscItem(String name, int compOutput, Supplier<SoundEvent> event, Item.Properties props, int length) {
        Supplier<T> musicDisc = (Supplier<T>) registerItem(name, () ->
                new RecordItem(compOutput, event, props, length * 20));
        return musicDisc;
    }

    public static <T extends Structure> void registerStructureType(String name, StructureType<T> structureType) {
        STRUCTURE_TYPES.register(name, () -> structureType);
    }

    public static void registerParticleType(String name, SimpleParticleType particleType) {
        PARTICLES.register(name, () -> particleType);
    }

    public static void registerStructureProcessor(String name, StructureProcessorType<?> processorType) {
        STRUCTURE_PROCESSOR_TYPES.register(name, () -> processorType);
    }

    public static void registerScreenHandlerType(String name, MenuType<?> screenHandlerType) {
        MENUS.register(name, () -> screenHandlerType);
    }

    public static void registerRenderType(RenderType type, Block... blocks) {
        // done block model JSON
    }

    public static <T extends SoundEvent> Supplier<T> registerSoundEvent(String name, Supplier<T> soundEvent) {
        return SOUNDS.register(name, soundEvent);
    }

    public static <T extends Entity> Supplier<EntityType<T>> registerEntityType(String name, Supplier<EntityType<T>> entityType) {
        return ENTITIES.register(name, entityType);
    }

    public static void registerEntityModelLayer(ModelLayerLocation location, Supplier<LayerDefinition> definition) {
        ENTITY_MODEL_LAYERS.put(location, definition);
    }

    public static <T extends Entity> void registerEntityRenderer(Supplier<EntityType<T>> type, EntityRendererProvider<T> renderProvider) {
        EntityRenderers.register(type.get(), renderProvider);
    }

    public static <T extends BlockEntity> void registerBlockEntityRenderer(Supplier<BlockEntityType<T>> type, BlockEntityRendererProvider<T> renderProvider) {
        // registered via event
    }

    public static void registerEntityAttribute(Supplier<? extends EntityType<? extends LivingEntity>> type, Supplier<AttributeSupplier.Builder> attribute) {
        ENTITY_ATTRIBUTES.put(type, attribute);
    }

    public static <T extends Recipe<?>> Supplier<RecipeType<T>> registerRecipeType(String name, RecipeType<T> type) {
        return RECIPE_TYPES.register(name, () -> type);
    }

    public static <S extends Recipe<?>> Supplier<RecipeSerializer<S>> registerRecipeSerializer(String name, RecipeSerializer<S> serializer) {
        return RECIPE_SERIALIZERS.register(name, () -> serializer);
    }

    public static <T extends TrunkPlacer> Supplier<TrunkPlacerType<?>> registerTrunkPlacerType(String name, Codec<T> codec) {
        return TRUNK_PLACERS.register(name, () -> new TrunkPlacerType<>(codec));
    }

    public static Supplier<Feature<?>> registerFeature(String name, Feature<?> feature) {
        return FEATURES.register(name, () -> feature);
    }

    public static <T extends CriterionTrigger<?>> Supplier<T> registerCriterion(String name, T criterion) {
        return CRITERIA.register(name, () -> criterion);
    }

}
