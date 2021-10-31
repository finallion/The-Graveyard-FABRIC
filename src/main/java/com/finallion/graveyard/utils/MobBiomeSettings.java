package com.finallion.graveyard.utils;

import com.finallion.graveyard.init.TGEntities;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.world.biome.Biome;

public class MobBiomeSettings {

    public static void init() {

        BiomeModifications.addSpawn(BiomeSelectors.all(),
                SpawnGroup.MONSTER, TGEntities.GHOUL, 7, 2, 4);

    }
}
