package com.lion.graveyard.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.advancements.CriterionTrigger;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.SimpleParticleType;
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
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public class RegistryHelper {

    @ExpectPlatform
    public static <T extends Block> Supplier<T> registerBlock(String name, Supplier<T> block) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends BlockEntityType<?>> Supplier<T> registerBlockEntity(String name, Supplier<T> blockEntity) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerItemGroup(
            ResourceKey<CreativeModeTab> registryKey,
            String name,
            String literalName,
            Item item
    ) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends Item> Supplier<T> registerItem(String name, Supplier<T> item) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerItemGroupWithIcon(
            ResourceKey<CreativeModeTab> registryKey,
            String name,
            String literalName,
            Supplier<ItemStack> icon
    ) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void addToItemGroup(ResourceKey<CreativeModeTab> itemGroup, Item item) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void addStacksToItemGroup(ResourceKey<CreativeModeTab> itemGroup, Supplier<List<ItemStack>> stacks) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends Item> Supplier<T> registerSpawnEggItem(
            String name,
            Supplier<? extends EntityType<? extends Mob>> type,
            int backgroundColor,
            int highlightColor,
            Item.Properties props
    ) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends Item> Supplier<T> registerMusicDiscItem(String name, Item.Properties props) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends Structure> void registerStructureType(String name, StructureType<T> structureType) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerParticleType(String name, SimpleParticleType particleType) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerStructureProcessor(String name, StructureProcessorType<?> processorType) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerCriterion(String name, CriterionTrigger<?> trigger) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerRenderType(RenderType type, Block... blocks) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerScreenHandlerType(String name, MenuType<?> screenHandlerType) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends SoundEvent> Supplier<T> registerSoundEvent(String name, Supplier<T> soundEvent) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends Entity> Supplier<EntityType<T>> registerEntityType(
            String name,
            Supplier<EntityType<T>> entityType
    ) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerEntityAttribute(
            Supplier<? extends EntityType<? extends LivingEntity>> type,
            Supplier<AttributeSupplier.Builder> attribute
    ) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerEntityModelLayer(ModelLayerLocation location, Supplier<LayerDefinition> definition) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends Entity> void registerEntityRenderer(
            Supplier<EntityType<T>> type,
            EntityRendererProvider<T> renderProvider
    ) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends BlockEntity> void registerBlockEntityRenderer(
            Supplier<BlockEntityType<T>> type,
            BlockEntityRendererProvider<T> renderProvider
    ) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends Recipe<?>> Supplier<RecipeType<T>> registerRecipeType(String name, RecipeType<T> type) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <S extends Recipe<?>> Supplier<RecipeSerializer<S>> registerRecipeSerializer(
            String name,
            RecipeSerializer<S> serializer
    ) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T> Supplier<DataComponentType<T>> registerDataComponent(
            String name,
            Supplier<DataComponentType<T>> type
    ) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends Mob> void registerSpawnPlacement(
            Supplier<? extends EntityType<T>> type,
            SpawnPlacementType placement,
            Heightmap.Types heightmap,
            SpawnPlacements.SpawnPredicate<T> predicate
    ) {
        throw new AssertionError();
    }

    /// Returns the same type it was handed, so the caller can keep it without asking the registry back.
    @ExpectPlatform
    public static LootItemConditionType registerLootCondition(String name, LootItemConditionType type) {
        throw new AssertionError();
    }
}
