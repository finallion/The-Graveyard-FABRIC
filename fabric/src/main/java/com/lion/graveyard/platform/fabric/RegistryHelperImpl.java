package com.lion.graveyard.platform.fabric;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.mixin.TrunkPlacerTypeInvoker;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.behavior.declarative.Trigger;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
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

import java.util.function.Supplier;

public class RegistryHelperImpl {

    public static <T extends Block> Supplier<T> registerBlock(String name, Supplier<T> block) {
        var registry = Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(Graveyard.MOD_ID, name), block.get());
        return () -> registry;
    }

    public static <T extends Item> Supplier<T> registerItem(String name, Supplier<T> item) {
        var registry = Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Graveyard.MOD_ID, name), item.get());
        return () -> registry;
    }

    public static void registerItemGroup(ResourceKey<CreativeModeTab> registryKey, String name, String literalName, Item item) {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, registryKey,
                FabricItemGroup.builder()
                        .title(Component.literal(literalName))
                        .icon(() -> new ItemStack(item)).build());
    }

    public static void addToItemGroup(ResourceKey<CreativeModeTab> itemGroup, Item item) {
        ItemGroupEvents.modifyEntriesEvent(itemGroup).register((content) -> content.prepend(item.getDefaultInstance()));
    }

    public static <T extends Item> Supplier<T> registerSpawnEggItem(String name, Supplier<? extends EntityType<? extends Mob>> type, int backgroundColor, int highlightColor, Item.Properties props) {
        return (Supplier<T>) registerItem(name, () -> new SpawnEggItem(type.get(), backgroundColor, highlightColor, props));
    }

    public static <T extends Item> Supplier<T> registerMusicDiscItem(String name, int compOutput, Supplier<SoundEvent> event, Item.Properties props, int length) {
        return (Supplier<T>) registerItem(name, () -> new RecordItem(compOutput, event.get(), props, length));
    }

    public static <T extends BlockEntityType<?>> Supplier<T> registerBlockEntity(String name, Supplier<T> blockEntity) {
        var registry = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(Graveyard.MOD_ID, name), blockEntity.get());
        return () -> registry;
    }

    public static <T extends Structure> void registerStructureType(String name, StructureType<T> structureType) {
        Registry.register(BuiltInRegistries.STRUCTURE_TYPE, new ResourceLocation(Graveyard.MOD_ID, name), structureType);
    }

    public static void registerParticleType(String name, SimpleParticleType particleType) {
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, Graveyard.createStringID(name), particleType);
    }

    public static void registerStructureProcessor(String name, StructureProcessorType<?> processorType) {
        Registry.register(BuiltInRegistries.STRUCTURE_PROCESSOR, new ResourceLocation(Graveyard.MOD_ID, name), processorType);
    }

    public static void registerScreenHandlerType(String name, MenuType<?> screenHandlerType) {
        Registry.register(BuiltInRegistries.MENU, new ResourceLocation(Graveyard.MOD_ID, name), screenHandlerType);
    }

    public static void registerRenderType(RenderType type, Block... blocks) {
        BlockRenderLayerMap.INSTANCE.putBlocks(type, blocks);
    }

    public static <T extends SoundEvent> Supplier<T> registerSoundEvent(String name, Supplier<T> soundEvent) {
        var registry = Registry.register(BuiltInRegistries.SOUND_EVENT, new ResourceLocation(Graveyard.MOD_ID, name), soundEvent.get());
        return () -> registry;
    }

    public static <T extends Entity> void registerEntityRenderer(Supplier<EntityType<T>> type, EntityRendererProvider<T> renderProvider) {
        EntityRendererRegistry.register(type.get(), renderProvider);
    }

    public static <T extends BlockEntity> void registerBlockEntityRenderer(Supplier<BlockEntityType<T>> type, BlockEntityRendererProvider<T> renderProvider) {
        BlockEntityRendererRegistry.register(type.get(), renderProvider);
    }

    public static <T extends Entity> Supplier<EntityType<T>> registerEntityType(String name, Supplier<EntityType<T>> entityType) {
        var registry = Registry.register(BuiltInRegistries.ENTITY_TYPE, new ResourceLocation(Graveyard.MOD_ID, name), entityType.get());
        return () -> registry;
    }

    public static void registerEntityModelLayer(ModelLayerLocation location, Supplier<LayerDefinition> definition) {
        EntityModelLayerRegistry.registerModelLayer(location, definition::get);
    }

    public static void registerEntityAttribute(Supplier<? extends EntityType<? extends LivingEntity>> type, Supplier<AttributeSupplier.Builder> attribute) {
        FabricDefaultAttributeRegistry.register(type.get(), attribute.get());
    }

    public static <T extends Recipe<?>> Supplier<RecipeType<T>> registerRecipeType(String name, RecipeType<T> type) {
        var registry = Registry.register(BuiltInRegistries.RECIPE_TYPE, new ResourceLocation(Graveyard.MOD_ID, name), type);
        return () -> registry;
    }

    public static <S extends Recipe<?>> Supplier<RecipeSerializer<S>> registerRecipeSerializer(String name, RecipeSerializer<S> serializer) {
        var registry = RecipeSerializer.register(Graveyard.createStringID(name), serializer);
        return () -> registry;
    }

    public static Supplier<Feature<?>> registerFeature(String name, Feature<?> feature) {
        var registry = Registry.register(BuiltInRegistries.FEATURE, new ResourceLocation(Graveyard.MOD_ID, name), feature);
        return () -> registry;
    }

    public static <T extends TrunkPlacer> Supplier<TrunkPlacerType<?>> registerTrunkPlacerType(String name, Codec<T> codec) {
        return () -> TrunkPlacerTypeInvoker.callRegister(Graveyard.createStringID(name), codec);
    }

    public static <T extends CriterionTrigger<?>> Supplier<T> registerCriterion(String name, T criterion) {
        var registry = CriteriaTriggers.register(Graveyard.createStringID(name), criterion);
        return () -> registry;
    }
}
