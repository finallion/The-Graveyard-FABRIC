package com.lion.graveyard.platform.fabric;

import com.lion.graveyard.Graveyard;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
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
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class RegistryHelperImpl {

    public static <T extends Block> Supplier<T> registerBlock(
            String name,
            Function<BlockBehaviour.Properties, T> factory,
            BlockBehaviour.Properties properties
    ) {
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, Graveyard.id(name));
        var registry = Registry.register(BuiltInRegistries.BLOCK, key, factory.apply(properties.setId(key)));
        return () -> registry;
    }

    public static <T extends Item> Supplier<T> registerItem(
            String name,
            Function<Item.Properties, T> factory,
            Item.Properties properties
    ) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Graveyard.id(name));
        var registry = Registry.register(BuiltInRegistries.ITEM, key, factory.apply(properties.setId(key)));
        return () -> registry;
    }

    public static void registerItemGroup(
            ResourceKey<CreativeModeTab> registryKey,
            String name,
            String literalName,
            Item item
    ) {
        Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB,
                registryKey,
                FabricCreativeModeTab.builder()
                        .title(Component.literal(literalName))
                        .icon(() -> new ItemStack(item)).build()
        );
    }

    public static void registerItemGroupWithIcon(
            ResourceKey<CreativeModeTab> registryKey,
            String name,
            String literalName,
            Supplier<ItemStack> icon
    ) {
        Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB,
                registryKey,
                FabricCreativeModeTab.builder()
                        .title(Component.literal(literalName))
                        .icon(icon).build()
        );
    }

    public static void addToItemGroup(ResourceKey<CreativeModeTab> itemGroup, Item item) {
        CreativeModeTabEvents.modifyOutputEvent(itemGroup).register((content) -> content.accept(item.getDefaultInstance()));
    }

    public static void addStacksToItemGroup(ResourceKey<CreativeModeTab> itemGroup, Supplier<List<ItemStack>> stacks) {
        CreativeModeTabEvents.modifyOutputEvent(itemGroup).register((content) -> stacks.get().forEach(content::accept));
    }

    public static <T extends Item> Supplier<T> registerSpawnEggItem(
            String name,
            Supplier<? extends EntityType<? extends Mob>> type,
            Item.Properties props
    ) {
        return (Supplier<T>) registerItem(name, itemProperties -> new SpawnEggItem(itemProperties.spawnEgg(type.get())), props);
    }

    public static <T extends Item> Supplier<T> registerMusicDiscItem(String name, Item.Properties props) {
        return (Supplier<T>) registerItem(name, Item::new, props);
    }

    public static <T extends BlockEntityType<?>> Supplier<T> registerBlockEntity(String name, Supplier<T> blockEntity) {
        var registry = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Graveyard.id(name), blockEntity.get());
        return () -> registry;
    }

    public static <T extends Structure> void registerStructureType(String name, StructureType<T> structureType) {
        Registry.register(BuiltInRegistries.STRUCTURE_TYPE, Graveyard.id(name), structureType);
    }

    public static void registerParticleType(String name, SimpleParticleType particleType) {
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, Graveyard.id(name), particleType);
    }

    public static void registerStructureProcessor(String name, StructureProcessorType<?> processorType) {
        Registry.register(BuiltInRegistries.STRUCTURE_PROCESSOR, Graveyard.id(name), processorType);
    }

    public static void registerCriterion(String name, CriterionTrigger<?> trigger) {
        Registry.register(BuiltInRegistries.TRIGGER_TYPES, Graveyard.id(name), trigger);
    }

    public static <T> Supplier<DataComponentType<T>> registerDataComponent(
            String name,
            Supplier<DataComponentType<T>> type
    ) {
        var registry = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Graveyard.id(name), type.get());
        return () -> registry;
    }

    public static <T extends Mob> void registerSpawnPlacement(
            Supplier<? extends EntityType<T>> type,
            SpawnPlacementType placement,
            Heightmap.Types heightmap,
            SpawnPlacements.SpawnPredicate<T> predicate
    ) {
        SpawnPlacements.register(type.get(), placement, heightmap, predicate);
    }

    public static void registerScreenHandlerType(String name, MenuType<?> screenHandlerType) {
        Registry.register(BuiltInRegistries.MENU, Graveyard.id(name), screenHandlerType);
    }

    public static <T extends SoundEvent> Supplier<T> registerSoundEvent(String name, Supplier<T> soundEvent) {
        var registry = Registry.register(BuiltInRegistries.SOUND_EVENT, Graveyard.id(name), soundEvent.get());
        return () -> registry;
    }

    public static <T extends Entity> void registerEntityRenderer(
            Supplier<EntityType<T>> type,
            EntityRendererProvider<T> renderProvider
    ) {
        EntityRendererRegistry.register(type.get(), renderProvider);
    }

    public static <T extends BlockEntity, S extends BlockEntityRenderState> void registerBlockEntityRenderer(
            Supplier<BlockEntityType<T>> type,
            BlockEntityRendererProvider<T, S> renderProvider
    ) {
        BlockEntityRendererRegistry.register(type.get(), renderProvider);
    }

    public static <T extends Entity> Supplier<EntityType<T>> registerEntityType(
            String name,
            EntityType.Builder<T> builder
    ) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, Graveyard.id(name));
        var registry = Registry.register(BuiltInRegistries.ENTITY_TYPE, key, builder.build(key));
        return () -> registry;
    }

    public static void registerEntityModelLayer(ModelLayerLocation location, Supplier<LayerDefinition> definition) {
        ModelLayerRegistry.registerModelLayer(location, definition::get);
    }

    public static void registerEntityAttribute(
            Supplier<? extends EntityType<? extends LivingEntity>> type,
            Supplier<AttributeSupplier.Builder> attribute
    ) {
        FabricDefaultAttributeRegistry.register(type.get(), attribute.get());
    }

    public static <T extends Recipe<?>> Supplier<RecipeType<T>> registerRecipeType(String name, RecipeType<T> type) {
        var registry = Registry.register(BuiltInRegistries.RECIPE_TYPE, Graveyard.id(name), type);
        return () -> registry;
    }

    public static <S extends Recipe<?>> Supplier<RecipeSerializer<S>> registerRecipeSerializer(
            String name,
            RecipeSerializer<S> serializer
    ) {
        var registry = Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Graveyard.id(name), serializer);
        return () -> registry;
    }

    public static <T extends LootItemCondition> MapCodec<T> registerLootCondition(String name, MapCodec<T> codec) {
        return Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, Graveyard.id(name), codec);
    }
}
