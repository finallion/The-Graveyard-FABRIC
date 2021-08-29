package com.finallion.graveyard.utils;

import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;

import java.util.function.Predicate;

public class StructureGenerationUtil {

    public static Predicate<BiomeSelectionContext> booleanToPredicate(boolean bol) {
        return (context) -> bol;
    }
}
