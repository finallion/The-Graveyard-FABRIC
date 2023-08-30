package com.lion.graveyard.platform.forge;

import com.lion.graveyard.Graveyard;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderers;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.*;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleType;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;
import net.minecraft.world.gen.trunk.TrunkPlacerType;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class RegistryHelperImpl {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Graveyard.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Graveyard.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Graveyard.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Graveyard.MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Graveyard.MOD_ID);
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister.create(RegistryKeys.STRUCTURE_TYPE, Graveyard.MOD_ID);
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Graveyard.MOD_ID);
    public static final DeferredRegister<ScreenHandlerType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Graveyard.MOD_ID);
    public static final DeferredRegister<ItemGroup> CREATIVE_TABS = DeferredRegister.create(RegistryKeys.ITEM_GROUP, Graveyard.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, Graveyard.MOD_ID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Graveyard.MOD_ID);
    public static final DeferredRegister<TrunkPlacerType<?>> TRUNK_PLACERS = DeferredRegister.create(RegistryKeys.TRUNK_PLACER_TYPE, Graveyard.MOD_ID);
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, Graveyard.MOD_ID);


    public static final Map<EntityModelLayer, Supplier<TexturedModelData>> ENTITY_MODEL_LAYERS = new ConcurrentHashMap<>();
    public static final Map<Supplier<? extends EntityType<? extends LivingEntity>>, Supplier<DefaultAttributeContainer.Builder>> ENTITY_ATTRIBUTES = new ConcurrentHashMap<>();
    public static final HashMap<RegistryKey<ItemGroup>, List<Item>> ITEMS_TO_ADD = new HashMap<>();

    public static <T extends Block> Supplier<T> registerBlock(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }

    public static <T extends BlockEntityType<?>> Supplier<T> registerBlockEntities(String name, Supplier<T> blockEntity) {
        return TILE_ENTITIES.register(name, blockEntity);
    }

    public static void registerItemGroup(RegistryKey<ItemGroup> registryKey, String name, String literalName, Item item) {
        CREATIVE_TABS.register(name, () -> new ItemGroup.Builder(ItemGroup.Row.TOP,0)
                .icon(() -> new ItemStack(item))
                .displayName(Text.literal(literalName)).build());
    }

    public static <T extends Item> Supplier<T> registerItem(String name, Supplier<T> item) {
        return ITEMS.register(name, item);
    }
    public static void addToItemGroup(RegistryKey<ItemGroup> itemGroup, Item item) {
        if (ITEMS_TO_ADD.containsKey(itemGroup)) {
            ITEMS_TO_ADD.get(itemGroup).add(item);
        } else {
            List<Item> items = new ArrayList<>();
            items.add(item);
            ITEMS_TO_ADD.put(itemGroup, items);
        }
    }

    public static <T extends Item> Supplier<T> registerSpawnEggItem(String name, Supplier<? extends EntityType<? extends MobEntity>> type, int backgroundColor, int highlightColor, Item.Settings props) {
        return (Supplier<T>) registerItem(name, () -> new ForgeSpawnEggItem(type, backgroundColor, highlightColor, props));
    }

    public static <T extends Item> Supplier<T> registerMusicDiscItem(String name, int compOutput, SoundEvent event, Item.Settings props, int length) {
        return (Supplier<T>) registerItem(name, () -> new MusicDiscItem(compOutput, () -> event, props, length * 20));
    }

    public static <T extends Structure> void registerStructureType(String name, StructureType<T> structureType) {
        STRUCTURE_TYPES.register(name, () -> structureType);
    }

    public static void registerParticleType(String name, DefaultParticleType particleType) {
        PARTICLES.register(name, () -> particleType);
    }

    public static void registerStructureProcessor(String name, StructureProcessorType<?> processorType) {
        Registry.register(Registries.STRUCTURE_PROCESSOR, new Identifier(Graveyard.MOD_ID, name), processorType);
    }

    public static void registerScreenHandlerType(String name, ScreenHandlerType<?> screenHandlerType) {
        MENUS.register(name, () -> screenHandlerType);
    }

    public static void registerRenderType(RenderLayer type, Block... blocks) {
        // TODO done in JSON model
        for (Block block : blocks) {
            RenderLayers.setRenderLayer(block, type);
        }
    }

    public static <T extends SoundEvent> Supplier<T> registerSoundEvent(String name, Supplier<T> soundEvent) {
        return SOUNDS.register(name, soundEvent);
    }

    public static <T extends Entity> Supplier<EntityType<T>> registerEntityType(String name, Supplier<EntityType<T>> entityType) {
        return ENTITIES.register(name, entityType);
    }

    public static void registerEntityModelLayer(EntityModelLayer location, Supplier<TexturedModelData> definition) {
        ENTITY_MODEL_LAYERS.put(location, definition);
    }

    public static <T extends Entity> void registerEntityRenderer(Supplier<EntityType<T>> type, EntityRendererFactory<T> renderProvider) {
        EntityRenderers.register(type.get(), renderProvider);
    }

    public static <T extends BlockEntity> void registerBlockEntityRenderer(Supplier<BlockEntityType<T>> type, BlockEntityRendererFactory<T> renderProvider) {
        // registered via event
    }

    public static void registerEntityAttribute(Supplier<? extends EntityType<? extends LivingEntity>> type, Supplier<DefaultAttributeContainer.Builder> attribute) {
        ENTITY_ATTRIBUTES.put(type, attribute);
    }

    public static <T extends Recipe<?>> Supplier<RecipeType<T>> registerRecipeType(String name, RecipeType<T> type) {
        return RECIPE_TYPES.register(name, () -> type);
    }

    public static <S extends Recipe<?>> Supplier<RecipeSerializer<S>> registerRecipeSerializer(String name, RecipeSerializer<S> serializer) {
        return RECIPE_SERIALIZERS.register(name, () -> serializer);
    }

    /*
    public static Supplier<TrunkPlacerType<?>> registerTrunkPlacer(String name, TrunkPlacerType<?> type) {
        return TRUNK_PLACERS.register(name, () -> type);
    }
     */

    public static Supplier<Feature<?>> registerFeature(String name, Feature<?> feature) {
        return FEATURES.register(name, () -> feature);
    }

}
