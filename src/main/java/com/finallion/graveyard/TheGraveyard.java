package com.finallion.graveyard;

import com.finallion.graveyard.config.TheGraveyardConfig;
import com.finallion.graveyard.init.*;
import com.finallion.graveyard.utils.StructureGenerationUtil;
import com.finallion.graveyard.utils.TGBiomeKeys;
import draylar.omegaconfig.OmegaConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;


public class TheGraveyard implements ModInitializer {
    public static final String MOD_ID = "graveyard";
    public static final TheGraveyardConfig config = OmegaConfig.register(TheGraveyardConfig.class);


    // halloween
    // swamp
    // more mushrooms
    // underwater
    // portal
    // mausoleum
    // crypt
    // ossuary
    // large trees
    // normal trees with bones
    // grave of the giant / skeleton
    // ziggurat
    // dragon graveyard


    @Override
    public void onInitialize() {
        TGBlocks.registerBlocks();
        TGItems.registerItems();
        TGStructures.setupAndRegisterStructureFeatures();
        TGConfiguredFeatures.registerConfiguredStructures();
        TGBiomeKeys.init();
        TGProcessors.init();



        BiomeModifications.create(new Identifier(MOD_ID, "small_walled_graveyard"))
                .add(ModificationPhase.ADDITIONS,
                        BiomeSelectors.categories(Biome.Category.PLAINS).and(StructureGenerationUtil.booleanToPredicate(config.enabled(new Identifier(MOD_ID, "small_walled_graveyard")))),
                        context -> { context.getGenerationSettings().addBuiltInStructure(TGConfiguredFeatures.CONFIGURED_SMALL_WALLED_GRAVEYARD);
                        });

        BiomeModifications.create(new Identifier(MOD_ID, "small_walled_graveyard_savanna"))
                .add(ModificationPhase.ADDITIONS,
                        BiomeSelectors.categories(Biome.Category.SAVANNA).and(StructureGenerationUtil.booleanToPredicate(config.enabled(new Identifier(MOD_ID, "small_walled_graveyard_savanna")))),
                        context -> { context.getGenerationSettings().addBuiltInStructure(TGConfiguredFeatures.CONFIGURED_SMALL_WALLED_GRAVEYARD_SAVANNA);
                        });


        BiomeModifications.create(new Identifier(MOD_ID, "small_grave"))
                .add(ModificationPhase.ADDITIONS,
                        BiomeSelectors.categories(Biome.Category.PLAINS, Biome.Category.FOREST, Biome.Category.TAIGA).and(StructureGenerationUtil.booleanToPredicate(config.enabled(new Identifier(MOD_ID, "small_grave")))),
                        context -> { context.getGenerationSettings().addBuiltInStructure(TGConfiguredFeatures.CONFIGURED_SMALL_GRAVE);
                        });


        BiomeModifications.create(new Identifier(MOD_ID, "mushroom_grave"))
                .add(ModificationPhase.ADDITIONS,
                        BiomeSelectors.categories(Biome.Category.MUSHROOM, Biome.Category.JUNGLE, Biome.Category.SWAMP).and(StructureGenerationUtil.booleanToPredicate(config.enabled(new Identifier(MOD_ID, "mushroom_grave")))),
                        context -> { context.getGenerationSettings().addBuiltInStructure(TGConfiguredFeatures.CONFIGURED_MUSHROOM_GRAVE);
                        });


        BiomeModifications.create(new Identifier(MOD_ID, "large_birch_tree"))
                .add(ModificationPhase.ADDITIONS,
                        BiomeSelectors.includeByKey(TGBiomeKeys.birch_biomes).and(StructureGenerationUtil.booleanToPredicate(config.enabled(new Identifier(MOD_ID, "large_birch_tree")))),
                        context -> { context.getGenerationSettings().addBuiltInStructure(TGConfiguredFeatures.CONFIGURED_LARGE_BIRCH_TREE);
                        });


        BiomeModifications.create(new Identifier(MOD_ID, "medium_walled_graveyard"))
                .add(ModificationPhase.ADDITIONS,
                        BiomeSelectors.categories(Biome.Category.FOREST).and(StructureGenerationUtil.booleanToPredicate(config.enabled(new Identifier(MOD_ID, "medium_walled_graveyard")))),
                        context -> { context.getGenerationSettings().addBuiltInStructure(TGConfiguredFeatures.CONFIGURED_MEDIUM_WALLED_GRAVEYARD);
                        });

        BiomeModifications.create(new Identifier(MOD_ID, "small_walled_graveyard_desert"))
                .add(ModificationPhase.ADDITIONS,
                        BiomeSelectors.categories(Biome.Category.DESERT).and(StructureGenerationUtil.booleanToPredicate(config.enabled(new Identifier(MOD_ID, "small_walled_graveyard_desert")))),
                        context -> { context.getGenerationSettings().addBuiltInStructure(TGConfiguredFeatures.CONFIGURED_SMALL_WALLED_GRAVEYARD_DESERT);
                        });



        BiomeModifications.create(new Identifier(MOD_ID, "large_walled_graveyard"))
                .add(ModificationPhase.ADDITIONS,
                        BiomeSelectors.all().and(StructureGenerationUtil.booleanToPredicate(config.enabled(new Identifier(MOD_ID, "large_walled_graveyard")))),
                        context -> { context.getGenerationSettings().addBuiltInStructure(TGConfiguredFeatures.CONFIGURED_LARGE_WALLED_GRAVEYARD);
                        });





    }

    public static ItemGroup GROUP = FabricItemGroupBuilder.create(
            new Identifier(MOD_ID, "group"))
            .icon(() -> new ItemStack(Items.SKELETON_SKULL)).build();



}
