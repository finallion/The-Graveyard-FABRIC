package com.finallion.graveyard.init.structureKeys;

import com.finallion.graveyard.TheGraveyard;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.structure.StructureType;

public class TGStructureTypeKeys {
    public static RegistryKey<StructureType> HAUNTED_HOUSE = of("haunted_house");
    public static RegistryKey<StructureType> LARGE_GRAVEYARD = of("large_graveyard");
    public static RegistryKey<StructureType> MEDIUM_GRAVEYARD = of("medium_graveyard");
    public static RegistryKey<StructureType> SMALL_GRAVEYARD = of("small_graveyard");
    public static RegistryKey<StructureType> SMALL_DESERT_GRAVEYARD = of("small_desert_graveyard");
    public static RegistryKey<StructureType> SMALL_GRAVE = of("small_grave");
    public static RegistryKey<StructureType> SMALL_DESERT_GRAVE = of("small_desert_grave");
    public static RegistryKey<StructureType> SMALL_SAVANNA_GRAVE = of("small_savanna_grave");
    public static RegistryKey<StructureType> SMALL_MOUNTAIN_GRAVE = of("small_mountain_grave");
    public static RegistryKey<StructureType> MUSHROOM_GRAVE = of("mushroom_grave");
    public static RegistryKey<StructureType> MEMORIAL_TREE = of("memorial_tree");
    public static RegistryKey<StructureType> ALTAR = of("altar");
    public static RegistryKey<StructureType> GIANT_MUSHROOM = of("giant_mushroom");
    public static RegistryKey<StructureType> CRYPT = of("crypt");

    private static RegistryKey<StructureType> of(String id) {
        return RegistryKey.of(Registry.STRUCTURE_KEY, new Identifier(TheGraveyard.MOD_ID, id));
    }

}
