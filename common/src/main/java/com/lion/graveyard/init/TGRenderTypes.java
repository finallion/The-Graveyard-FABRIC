package com.lion.graveyard.init;

import com.lion.graveyard.platform.RegistryHelper;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;

public class TGRenderTypes {
    private static final ChunkSectionLayer CUTOUT = ChunkSectionLayer.CUTOUT;

    public static void postInit() {
        RegistryHelper.registerRenderType(CUTOUT, TGOverrunVillage.CHARRED_DOOR.get());
        RegistryHelper.registerRenderType(CUTOUT, TGOverrunVillage.CHARRED_TRAPDOOR.get());
        RegistryHelper.registerRenderType(CUTOUT, TGOverrunVillage.SCORCHED_DOOR.get());
        RegistryHelper.registerRenderType(CUTOUT, TGOverrunVillage.SCORCHED_TRAPDOOR.get());
        RegistryHelper.registerRenderType(CUTOUT, TGBlocks.SKULL_WITH_RIB_CAGE.get());
        RegistryHelper.registerRenderType(CUTOUT, TGBlocks.WITHER_SKULL_WITH_RIB_CAGE.get());
        RegistryHelper.registerRenderType(CUTOUT, TGBlocks.LEANING_SKELETON.get());
        RegistryHelper.registerRenderType(CUTOUT, TGBlocks.LEANING_WITHER_SKELETON.get());
        RegistryHelper.registerRenderType(CUTOUT, TGBlocks.LYING_SKELETON.get());
        RegistryHelper.registerRenderType(CUTOUT, TGBlocks.LYING_WITHER_SKELETON.get());
        RegistryHelper.registerRenderType(CUTOUT, TGBlocks.BONE_REMAINS.get());
        RegistryHelper.registerRenderType(CUTOUT, TGBlocks.SKULL_ON_PIKE.get());
        RegistryHelper.registerRenderType(CUTOUT, TGBlocks.LATERALLY_LYING_SKELETON.get());
        RegistryHelper.registerRenderType(CUTOUT, TGBlocks.TORSO_PILE.get());
        RegistryHelper.registerRenderType(CUTOUT, TGBlocks.WITHER_BONE_REMAINS.get());
        RegistryHelper.registerRenderType(CUTOUT, TGBlocks.WITHER_SKULL_ON_PIKE.get());
        RegistryHelper.registerRenderType(CUTOUT, TGBlocks.LATERALLY_LYING_WITHER_SKELETON.get());
        RegistryHelper.registerRenderType(CUTOUT, TGBlocks.HANGED_SKELETON.get());
        RegistryHelper.registerRenderType(CUTOUT, TGBlocks.HANGED_WITHER_SKELETON.get());
        RegistryHelper.registerRenderType(CUTOUT, TGBlocks.WITHER_TORSO_PILE.get());
        RegistryHelper.registerRenderType(CUTOUT, TGBlocks.DARK_IRON_BARS.get());
        RegistryHelper.registerRenderType(CUTOUT, TGBlocks.TG_GRASS_BLOCK.get());
        RegistryHelper.registerRenderType(CUTOUT, TGBlocks.SOUL_FIRE_BRAZIER.get());
        RegistryHelper.registerRenderType(CUTOUT, TGBlocks.FIRE_BRAZIER.get());
        RegistryHelper.registerRenderType(CUTOUT, TGBlocks.CANDLE_HOLDER.get());
        RegistryHelper.registerRenderType(CUTOUT, TGBlocks.DARK_IRON_DOOR.get());
        RegistryHelper.registerRenderType(CUTOUT, TGBlocks.DARK_IRON_TRAPDOOR.get());
        RegistryHelper.registerRenderType(CUTOUT, TGBlocks.OSSUARY.get());

        RegistryHelper.registerRenderType(CUTOUT, TGBlocks.SEATED_FULL_SKELETON.get());
        RegistryHelper.registerRenderType(CUTOUT, TGBlocks.SEATED_FULL_WITHER_SKELETON.get());
        RegistryHelper.registerRenderType(CUTOUT, TGBlocks.SKELETON_SKULL_WITH_AXE.get());
        RegistryHelper.registerRenderType(CUTOUT, TGBlocks.WITHER_SKELETON_SKULL_WITH_AXE.get());
        RegistryHelper.registerRenderType(CUTOUT, TGBlocks.SWORD_IN_SKELETON.get());
        RegistryHelper.registerRenderType(CUTOUT, TGBlocks.SWORD_IN_WITHER_SKELETON.get());
        RegistryHelper.registerRenderType(CUTOUT, TGBlocks.SMALL_SKELETON_REMAINS.get());
        RegistryHelper.registerRenderType(CUTOUT, TGBlocks.SMALL_WITHER_SKELETON_REMAINS.get());
        RegistryHelper.registerRenderType(CUTOUT, TGBlocks.HANGED_FULL_SKELETON.get());
        RegistryHelper.registerRenderType(CUTOUT, TGBlocks.HANGED_FULL_WITHER_SKELETON.get());
        RegistryHelper.registerRenderType(CUTOUT, TGBlocks.LYING_FULL_SKELETON.get());
        RegistryHelper.registerRenderType(CUTOUT, TGBlocks.LYING_FULL_WITHER_SKELETON.get());
        RegistryHelper.registerRenderType(CUTOUT, TGBlocks.BACKSTABBED_FULL_SKELETON.get());
        RegistryHelper.registerRenderType(CUTOUT, TGBlocks.BACKSTABBED_FULL_WITHER_SKELETON.get());
        RegistryHelper.registerRenderType(CUTOUT, TGBlocks.SHOT_FULL_SKELETON.get());
        RegistryHelper.registerRenderType(CUTOUT, TGBlocks.SHOT_FULL_WITHER_SKELETON.get());
        RegistryHelper.registerRenderType(CUTOUT, TGBlocks.SIDEWAYS_FULL_SKELETON.get());
        RegistryHelper.registerRenderType(CUTOUT, TGBlocks.SIDEWAYS_FULL_WITHER_SKELETON.get());


    }


}
