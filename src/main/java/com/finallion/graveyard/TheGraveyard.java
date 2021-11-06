package com.finallion.graveyard;

import com.finallion.graveyard.config.TheGraveyardConfig;
import com.finallion.graveyard.init.*;
import com.finallion.graveyard.utils.FeatureBiomeSettings;
import com.finallion.graveyard.utils.MobBiomeSettings;
import com.finallion.graveyard.utils.StructureBiomeSettings;
import com.finallion.graveyard.utils.TGBiomeKeys;
import draylar.omegaconfig.OmegaConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.GeckoLib;


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
    // pirate ship -> jigsaw battle

    // vignette


    // sugar cane/bamboo plantable
    // check moss replacables




    @Override
    public void onInitialize() {
        GeckoLib.initialize();

        // TODO: change hitboxes on large urns
        TGBiomes.registerBiomes();
        TGConfiguredFeatures.registerFeatures();
        TGConfiguredFeatures.registerConfiguredFeatures();
        FeatureBiomeSettings.init();
        //MobBiomeSettings.init();
        TGBlocks.registerBlocks();
        TGItems.registerItems();
        TGStructures.setupAndRegisterStructureFeatures();
        TGConfiguredStructureFeatures.registerConfiguredStructures();
        TGBiomeKeys.init();
        TGProcessors.init();
        TGEntities.registerEntities();
        //TGVillagerTrades.init();
        StructureBiomeSettings.init();

    }

    public static ItemGroup GROUP = FabricItemGroupBuilder.create(
            new Identifier(MOD_ID, "group"))
            .icon(() -> new ItemStack(Items.SKELETON_SKULL)).build();



}
