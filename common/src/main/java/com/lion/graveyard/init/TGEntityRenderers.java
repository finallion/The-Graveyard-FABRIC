package com.lion.graveyard.init;

import com.lion.graveyard.entities.renders.*;
import com.lion.graveyard.platform.RegistryHelper;

public class TGEntityRenderers {

    public static void postInit() {
        RegistryHelper.registerEntityRenderer(TGEntities.SKELETON_CREEPER, SkeletonCreeperRender::new);
        RegistryHelper.registerEntityRenderer(TGEntities.ACOLYTE, AcolyteRender::new);
        RegistryHelper.registerEntityRenderer(TGEntities.GHOUL, GhoulRenderer::new);
        RegistryHelper.registerEntityRenderer(TGEntities.REAPER, ReaperRenderer::new);
        RegistryHelper.registerEntityRenderer(TGEntities.REVENANT, RevenantRenderer::new);
        RegistryHelper.registerEntityRenderer(TGEntities.NIGHTMARE, NightmareRenderer::new);
        RegistryHelper.registerEntityRenderer(TGEntities.CORRUPTED_PILLAGER, CorruptedPillagerRenderer::new);
        RegistryHelper.registerEntityRenderer(TGEntities.CORRUPTED_VINDICATOR, CorruptedVindicatorRenderer::new);
        RegistryHelper.registerEntityRenderer(TGEntities.WRAITH, WraithRenderer::new);
        RegistryHelper.registerEntityRenderer(TGEntities.LICH, LichRenderer::new);
        RegistryHelper.registerEntityRenderer(TGEntities.FALLING_CORPSE, FallingCorpseRenderer::new);
        RegistryHelper.registerEntityRenderer(TGEntities.SKULL, SkullEntityRenderer::new);
        RegistryHelper.registerEntityRenderer(TGEntities.GHOULING, GhoulingRenderer::new);
        RegistryHelper.registerEntityRenderer(TGEntities.NAMELESS_HANGED, NamelessHangedRenderer::new);
    }
}
