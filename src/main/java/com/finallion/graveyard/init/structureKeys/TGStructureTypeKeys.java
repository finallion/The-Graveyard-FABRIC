package com.finallion.graveyard.init.structureKeys;

import com.finallion.graveyard.TheGraveyard;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

public class TGStructureTypeKeys {
    public static RegistryKey<Structure> HAUNTED_HOUSE = of("haunted_house");
    public static RegistryKey<Structure> LARGE_GRAVEYARD = of("large_graveyard");
    public static RegistryKey<Structure> MEDIUM_GRAVEYARD = of("medium_graveyard");
    public static RegistryKey<Structure> SMALL_GRAVEYARD = of("small_graveyard");
    public static RegistryKey<Structure> SMALL_DESERT_GRAVEYARD = of("small_desert_graveyard");
    public static RegistryKey<Structure> SMALL_GRAVE = of("small_grave");
    public static RegistryKey<Structure> SMALL_DESERT_GRAVE = of("small_desert_grave");
    public static RegistryKey<Structure> SMALL_SAVANNA_GRAVE = of("small_savanna_grave");
    public static RegistryKey<Structure> SMALL_MOUNTAIN_GRAVE = of("small_mountain_grave");
    public static RegistryKey<Structure> MUSHROOM_GRAVE = of("mushroom_grave");
    public static RegistryKey<Structure> MEMORIAL_TREE = of("memorial_tree");
    public static RegistryKey<Structure> ALTAR = of("altar");
    public static RegistryKey<Structure> GIANT_MUSHROOM = of("giant_mushroom");
    public static RegistryKey<Structure> CRYPT = of("crypt");

    private static RegistryKey<Structure> of(String id) {
        return RegistryKey.of(Registry.STRUCTURE_KEY, new Identifier(TheGraveyard.MOD_ID, id));
    }

}
