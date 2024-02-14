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
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.*;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.ResourceType;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

import java.util.function.Supplier;

public class RegistryHelperImpl {

    public static <T extends Block> Supplier<T> registerBlock(String name, Supplier<T> block) {
        var registry = Registry.register(Registries.BLOCK, new Identifier(Graveyard.MOD_ID, name), block.get());
        return () -> registry;
    }

    public static <T extends Item> Supplier<T> registerItem(String name, Supplier<T> item) {
        var registry = Registry.register(Registries.ITEM, new Identifier(Graveyard.MOD_ID, name), item.get());
        return () -> registry;
    }

    public static void registerItemGroup(RegistryKey<ItemGroup> registryKey, String name, String literalName, Item item) {
        Registry.register(Registries.ITEM_GROUP, registryKey,
                FabricItemGroup.builder()
                        .displayName(Text.literal(literalName))
                        .icon(() -> new ItemStack(item)).build());
    }

    public static void addToItemGroup(RegistryKey<ItemGroup> itemGroup, Item item) {
        ItemGroupEvents.modifyEntriesEvent(itemGroup).register((content) -> content.add(item.getDefaultStack()));
    }

    public static <T extends Item> Supplier<T> registerSpawnEggItem(String name, Supplier<? extends EntityType<? extends MobEntity>> type, int backgroundColor, int highlightColor, Item.Settings props) {
        return (Supplier<T>) registerItem(name, () -> new SpawnEggItem(type.get(), backgroundColor, highlightColor, props));
    }

    public static <T extends Item> Supplier<T> registerMusicDiscItem(String name, int compOutput, Supplier<SoundEvent> event, Item.Settings props, int length) {
        return (Supplier<T>) registerItem(name, () -> new MusicDiscItem(compOutput, event.get(), props, length));
    }

    public static <T extends BlockEntityType<?>> Supplier<T> registerBlockEntity(String name, Supplier<T> blockEntity) {
        var registry = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(Graveyard.MOD_ID, name), blockEntity.get());
        return () -> registry;
    }

    public static <T extends Structure> void registerStructureType(String name, StructureType<T> structureType) {
        Registry.register(Registries.STRUCTURE_TYPE, new Identifier(Graveyard.MOD_ID, name), structureType);
    }

    public static void registerParticleType(String name, DefaultParticleType particleType) {
        Registry.register(Registries.PARTICLE_TYPE, Graveyard.createStringID(name), particleType);
    }

    public static void registerStructureProcessor(String name, StructureProcessorType<?> processorType) {
        Registry.register(Registries.STRUCTURE_PROCESSOR, new Identifier(Graveyard.MOD_ID, name), processorType);
    }

    public static void registerScreenHandlerType(String name, ScreenHandlerType<?> screenHandlerType) {
        Registry.register(Registries.SCREEN_HANDLER, new Identifier(Graveyard.MOD_ID, name), screenHandlerType);
    }

    public static void registerRenderType(RenderLayer type, Block... blocks) {
        BlockRenderLayerMap.INSTANCE.putBlocks(type, blocks);
    }

    public static <T extends SoundEvent> Supplier<T> registerSoundEvent(String name, Supplier<T> soundEvent) {
        var registry = Registry.register(Registries.SOUND_EVENT, new Identifier(Graveyard.MOD_ID, name), soundEvent.get());
        return () -> registry;
    }

    public static <T extends Entity> void registerEntityRenderer(Supplier<EntityType<T>> type, EntityRendererFactory<T> renderProvider) {
        EntityRendererRegistry.register(type.get(), renderProvider);
    }

    public static <T extends BlockEntity> void registerBlockEntityRenderer(Supplier<BlockEntityType<T>> type, BlockEntityRendererFactory<T> renderProvider) {
        BlockEntityRendererRegistry.register(type.get(), renderProvider);
    }

    public static <T extends Entity> Supplier<EntityType<T>> registerEntityType(String name, Supplier<EntityType<T>> entityType) {
        var registry = Registry.register(Registries.ENTITY_TYPE, new Identifier(Graveyard.MOD_ID, name), entityType.get());
        return () -> registry;
    }

    public static void registerEntityModelLayer(EntityModelLayer location, Supplier<TexturedModelData> definition) {
        EntityModelLayerRegistry.registerModelLayer(location, definition::get);
    }

    public static void registerEntityAttribute(Supplier<? extends EntityType<? extends LivingEntity>> type, Supplier<DefaultAttributeContainer.Builder> attribute) {
        FabricDefaultAttributeRegistry.register(type.get(), attribute.get());
    }

    public static <T extends Recipe<?>> Supplier<RecipeType<T>> registerRecipeType(String name, RecipeType<T> type) {
        var registry = Registry.register(Registries.RECIPE_TYPE, new Identifier(Graveyard.MOD_ID, name), type);
        return () -> registry;
    }

    public static <S extends Recipe<?>> Supplier<RecipeSerializer<S>> registerRecipeSerializer(String name, RecipeSerializer<S> serializer) {
        var registry = RecipeSerializer.register(Graveyard.createStringID(name), serializer);
        return () -> registry;
    }

    public static Supplier<Feature<?>> registerFeature(String name, Feature<?> feature) {
        var registry = Registry.register(Registries.FEATURE, new Identifier(Graveyard.MOD_ID, name), feature);
        return () -> registry;
    }

    public static <T extends TrunkPlacer> Supplier<TrunkPlacerType<?>> registerTrunkPlacerType(String name, Codec<T> codec) {
        return () -> TrunkPlacerTypeInvoker.callRegister(Graveyard.createStringID(name), codec);
    }

    public static <T extends Criterion<?>> Supplier<T> registerCriterion(String name, T criterion) {
        var registry = Criteria.register(Graveyard.createStringID(name), criterion);
        return () -> registry;
    }
}
