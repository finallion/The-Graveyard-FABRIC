package com.lion.graveyard.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

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
    public static <T extends Item> Supplier<T> registerItem(String name, Supplier<T> item) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends Structure> void registerStructureType(String name, StructureType<T> structureType) {
        throw new AssertionError();
    }


}
