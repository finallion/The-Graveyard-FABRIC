package com.finallion.graveyard.utils;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.init.TGConfiguredFeatures;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

public class StructureBiomeSettings {


    public static void init() {
        BiomeModifications.create(new Identifier(TheGraveyard.MOD_ID, "small_walled_graveyard"))
                .add(ModificationPhase.ADDITIONS,
                        BiomeSelectors.categories(Biome.Category.PLAINS).and(StructureGenerationUtil.booleanToPredicate(TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "small_walled_graveyard")))),
                        context -> { context.getGenerationSettings().addBuiltInStructure(TGConfiguredFeatures.CONFIGURED_SMALL_WALLED_GRAVEYARD);
                        });

        BiomeModifications.create(new Identifier(TheGraveyard.MOD_ID, "small_walled_graveyard_savanna"))
                .add(ModificationPhase.ADDITIONS,
                        BiomeSelectors.categories(Biome.Category.SAVANNA).and(StructureGenerationUtil.booleanToPredicate(TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "small_walled_graveyard_savanna")))),
                        context -> { context.getGenerationSettings().addBuiltInStructure(TGConfiguredFeatures.CONFIGURED_SMALL_WALLED_GRAVEYARD_SAVANNA);
                        });


        BiomeModifications.create(new Identifier(TheGraveyard.MOD_ID, "small_grave"))
                .add(ModificationPhase.ADDITIONS,
                        BiomeSelectors.categories(Biome.Category.PLAINS, Biome.Category.FOREST, Biome.Category.TAIGA).and(StructureGenerationUtil.booleanToPredicate(TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "small_grave")))),
                        context -> { context.getGenerationSettings().addBuiltInStructure(TGConfiguredFeatures.CONFIGURED_SMALL_GRAVE);
                        });


        BiomeModifications.create(new Identifier(TheGraveyard.MOD_ID, "mushroom_grave"))
                .add(ModificationPhase.ADDITIONS,
                        BiomeSelectors.categories(Biome.Category.MUSHROOM, Biome.Category.JUNGLE, Biome.Category.SWAMP).and(StructureGenerationUtil.booleanToPredicate(TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "mushroom_grave")))),
                        context -> { context.getGenerationSettings().addBuiltInStructure(TGConfiguredFeatures.CONFIGURED_MUSHROOM_GRAVE);
                        });


        BiomeModifications.create(new Identifier(TheGraveyard.MOD_ID, "large_birch_tree"))
                .add(ModificationPhase.ADDITIONS,
                        BiomeSelectors.includeByKey(TGBiomeKeys.birch_biomes).and(StructureGenerationUtil.booleanToPredicate(TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "large_birch_tree")))),
                        context -> { context.getGenerationSettings().addBuiltInStructure(TGConfiguredFeatures.CONFIGURED_LARGE_BIRCH_TREE);
                        });


        BiomeModifications.create(new Identifier(TheGraveyard.MOD_ID, "medium_walled_graveyard"))
                .add(ModificationPhase.ADDITIONS,
                        BiomeSelectors.categories(Biome.Category.FOREST, Biome.Category.TAIGA).and(StructureGenerationUtil.booleanToPredicate(TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "medium_walled_graveyard")))),
                        context -> { context.getGenerationSettings().addBuiltInStructure(TGConfiguredFeatures.CONFIGURED_MEDIUM_WALLED_GRAVEYARD);
                        });

        BiomeModifications.create(new Identifier(TheGraveyard.MOD_ID, "small_walled_graveyard_desert"))
                .add(ModificationPhase.ADDITIONS,
                        BiomeSelectors.categories(Biome.Category.DESERT).and(StructureGenerationUtil.booleanToPredicate(TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "small_walled_graveyard_desert")))),
                        context -> { context.getGenerationSettings().addBuiltInStructure(TGConfiguredFeatures.CONFIGURED_SMALL_WALLED_GRAVEYARD_DESERT);
                        });



        BiomeModifications.create(new Identifier(TheGraveyard.MOD_ID, "large_walled_graveyard"))
                .add(ModificationPhase.ADDITIONS,
                        BiomeSelectors.categories(Biome.Category.FOREST, Biome.Category.TAIGA, Biome.Category.JUNGLE).and(StructureGenerationUtil.booleanToPredicate(TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "large_walled_graveyard")))),
                        context -> { context.getGenerationSettings().addBuiltInStructure(TGConfiguredFeatures.CONFIGURED_LARGE_WALLED_GRAVEYARD);
                        });



        BiomeModifications.create(new Identifier(TheGraveyard.MOD_ID, "haunted_house"))
                .add(ModificationPhase.ADDITIONS,
                        BiomeSelectors.categories(Biome.Category.FOREST, Biome.Category.SWAMP).and(StructureGenerationUtil.booleanToPredicate(TheGraveyard.config.enabled(new Identifier(TheGraveyard.MOD_ID, "haunted_house")))),
                        context -> { context.getGenerationSettings().addBuiltInStructure(TGConfiguredFeatures.CONFIGURED_HAUNTED_HOUSE);
                        });

    }




}
