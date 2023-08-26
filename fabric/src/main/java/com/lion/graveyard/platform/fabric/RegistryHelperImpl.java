package com.lion.graveyard.platform.fabric;

import com.lion.graveyard.Graveyard;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

import java.util.function.Supplier;

public class RegistryHelperImpl {

    public static <T extends Block> Supplier<T> registerBlock(String name, Supplier<T> block) {
        return () -> Registry.register(Registries.BLOCK, new Identifier(Graveyard.MOD_ID, name), block.get());
    }

    public static <T extends Item> Supplier<T> registerItem(String name, Supplier<T> item) {
        return () -> Registry.register(Registries.ITEM, new Identifier(Graveyard.MOD_ID, name), item.get());
    }

    public static <T extends BlockEntityType<?>> Supplier<T> registerBlockEntity(String name, Supplier<T> blockEntity) {
        return () -> Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(Graveyard.MOD_ID, name), blockEntity.get());
    }

    public static <T extends Structure> void registerStructureType(String name, StructureType<T> structureType) {
        Registry.register(Registries.STRUCTURE_TYPE, new Identifier(Graveyard.MOD_ID, name), structureType);
    }



}
