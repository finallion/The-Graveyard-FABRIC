package com.finallion.graveyard.init.structureKeys;

import com.finallion.graveyard.TheGraveyard;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public class TGStructureSetKeys {
    public static RegistryKey<StructureSet> HAUNTED_HOUSES = of("haunted_house");
    public static RegistryKey<StructureSet> LARGE_GRAVEYARDS = of("large_graveyards");
    public static RegistryKey<StructureSet> MEDIUM_GRAVEYARDS = of("medium_graveyards");
    public static RegistryKey<StructureSet> SMALL_GRAVEYARDS = of("small_graveyards");
    public static RegistryKey<StructureSet> SMALL_DESERT_GRAVEYARDS = of("small_desert_graveyards");
    public static RegistryKey<StructureSet> SMALL_GRAVES = of("small_graves");
    public static RegistryKey<StructureSet> SMALL_DESERT_GRAVES = of("small_desert_graves");
    public static RegistryKey<StructureSet> SMALL_SAVANNA_GRAVES = of("small_savanna_graves");
    public static RegistryKey<StructureSet> SMALL_MOUNTAIN_GRAVES = of("small_mountain_graves");
    public static RegistryKey<StructureSet> MUSHROOM_GRAVES = of("mushroom_graves");
    public static RegistryKey<StructureSet> MEMORIAL_TREES = of("memorial_trees");

    private static RegistryKey<StructureSet> of(String id) {
        return RegistryKey.of(Registry.STRUCTURE_SET_KEY, new Identifier(TheGraveyard.MOD_ID, id));
    }
}
