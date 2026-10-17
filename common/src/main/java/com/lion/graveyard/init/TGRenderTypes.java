package com.lion.graveyard.init;

import com.lion.graveyard.platform.RegistryHelper;
import net.minecraft.client.renderer.RenderType;

public class TGRenderTypes {
    private static final RenderType CUTOUT_MIPPED = RenderType.cutoutMipped();

    public static void postInit() {
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGOverrunVillage.CHARRED_DOOR.get());
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGOverrunVillage.CHARRED_TRAPDOOR.get());
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGOverrunVillage.SCORCHED_DOOR.get());
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGOverrunVillage.SCORCHED_TRAPDOOR.get());
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGBlocks.SKULL_WITH_RIB_CAGE.get());
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGBlocks.WITHER_SKULL_WITH_RIB_CAGE.get());
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGBlocks.LEANING_SKELETON.get());
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGBlocks.LEANING_WITHER_SKELETON.get());
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGBlocks.LYING_SKELETON.get());
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGBlocks.LYING_WITHER_SKELETON.get());
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGBlocks.BONE_REMAINS.get());
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGBlocks.SKULL_ON_PIKE.get());
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGBlocks.LATERALLY_LYING_SKELETON.get());
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGBlocks.TORSO_PILE.get());
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGBlocks.WITHER_BONE_REMAINS.get());
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGBlocks.WITHER_SKULL_ON_PIKE.get());
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGBlocks.LATERALLY_LYING_WITHER_SKELETON.get());
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGBlocks.HANGED_SKELETON.get());
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGBlocks.HANGED_WITHER_SKELETON.get());
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGBlocks.WITHER_TORSO_PILE.get());
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGBlocks.DARK_IRON_BARS.get());
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGBlocks.TG_GRASS_BLOCK.get());
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGBlocks.SOUL_FIRE_BRAZIER.get());
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGBlocks.FIRE_BRAZIER.get());
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGBlocks.CANDLE_HOLDER.get());
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGBlocks.DARK_IRON_DOOR.get());
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGBlocks.DARK_IRON_TRAPDOOR.get());
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGBlocks.OSSUARY.get());

        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGBlocks.SEATED_FULL_SKELETON.get());
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGBlocks.SEATED_FULL_WITHER_SKELETON.get());
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGBlocks.SKELETON_SKULL_WITH_AXE.get());
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGBlocks.WITHER_SKELETON_SKULL_WITH_AXE.get());
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGBlocks.SWORD_IN_SKELETON.get());
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGBlocks.SWORD_IN_WITHER_SKELETON.get());
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGBlocks.SMALL_SKELETON_REMAINS.get());
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGBlocks.SMALL_WITHER_SKELETON_REMAINS.get());
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGBlocks.HANGED_FULL_SKELETON.get());
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGBlocks.HANGED_FULL_WITHER_SKELETON.get());
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGBlocks.LYING_FULL_SKELETON.get());
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGBlocks.LYING_FULL_WITHER_SKELETON.get());
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGBlocks.BACKSTABBED_FULL_SKELETON.get());
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGBlocks.BACKSTABBED_FULL_WITHER_SKELETON.get());
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGBlocks.SHOT_FULL_SKELETON.get());
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGBlocks.SHOT_FULL_WITHER_SKELETON.get());
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGBlocks.SIDEWAYS_FULL_SKELETON.get());
        RegistryHelper.registerRenderType(CUTOUT_MIPPED, TGBlocks.SIDEWAYS_FULL_WITHER_SKELETON.get());


    }


}
