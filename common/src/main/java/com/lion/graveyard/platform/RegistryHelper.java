package com.lion.graveyard.platform;

import com.lion.graveyard.world.trunk_placer.TGOakTreeTrunkPlacer;
import com.mojang.serialization.Codec;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

import java.util.function.Supplier;

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
    public static void registerItemGroup(RegistryKey<ItemGroup> registryKey, String name, String literalName, Item item) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends Item> Supplier<T> registerItem(String name, Supplier<T> item) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void addToItemGroup(RegistryKey<ItemGroup> itemGroup, Item item) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends Item> Supplier<T> registerSpawnEggItem(String name, Supplier<? extends EntityType<? extends MobEntity>> type, int backgroundColor, int highlightColor, Item.Settings props) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends Item> Supplier<T> registerMusicDiscItem(String name, int compOutput, Supplier<SoundEvent> event, Item.Settings props, int length) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends Structure> void registerStructureType(String name, StructureType<T> structureType) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerParticleType(String name, DefaultParticleType particleType) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerStructureProcessor(String name, StructureProcessorType<?> processorType) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerRenderType(RenderLayer type, Block... blocks) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerScreenHandlerType(String name, ScreenHandlerType<?> screenHandlerType) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends SoundEvent> Supplier<T> registerSoundEvent(String name, Supplier<T> soundEvent) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends Entity> Supplier<EntityType<T>> registerEntityType(String name, Supplier<EntityType<T>> entityType) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerEntityAttribute(Supplier<? extends EntityType<? extends LivingEntity>> type, Supplier<DefaultAttributeContainer.Builder> attribute) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerEntityModelLayer(EntityModelLayer location, Supplier<TexturedModelData> definition) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends Entity> void registerEntityRenderer(Supplier<EntityType<T>> type, EntityRendererFactory<T> renderProvider) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends BlockEntity> void registerBlockEntityRenderer(Supplier<BlockEntityType<T>> type, BlockEntityRendererFactory<T> renderProvider) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends Recipe<?>> Supplier<RecipeType<T>> registerRecipeType(String name, RecipeType<T> type) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <S extends Recipe<?>> Supplier<RecipeSerializer<S>> registerRecipeSerializer(String name, RecipeSerializer<S> serializer) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Supplier<Feature<?>> registerFeature(String name, Feature<?> feature) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends TrunkPlacer> Supplier<TrunkPlacerType<?>> registerTrunkPlacerType(String name, Codec<T> codec) {
        throw new AssertionError();
    }
}
