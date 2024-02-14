package com.lion.graveyard.init;

import com.lion.graveyard.blockentities.renders.BrazierBlockEntityRenderer;
import com.lion.graveyard.blockentities.renders.GravestoneBlockEntityRenderer;
import com.lion.graveyard.blockentities.renders.OssuaryBlockEntityRenderer;
import com.lion.graveyard.blockentities.renders.SarcophagusBlockEntityRenderer;
import com.lion.graveyard.platform.RegistryHelper;

public class TGBlockEntityRenderers {

    public static void postInit() {
        RegistryHelper.registerBlockEntityRenderer(TGBlockEntities.GRAVESTONE_BLOCK_ENTITY, GravestoneBlockEntityRenderer::new);
        RegistryHelper.registerBlockEntityRenderer(TGBlockEntities.SARCOPHAGUS_BLOCK_ENTITY, SarcophagusBlockEntityRenderer::new);
        RegistryHelper.registerBlockEntityRenderer(TGBlockEntities.BRAZIER_BLOCK_ENTITY, context -> new BrazierBlockEntityRenderer());
        RegistryHelper.registerBlockEntityRenderer(TGBlockEntities.OSSUARY_BLOCK_ENTITY, context -> new OssuaryBlockEntityRenderer());
    }
}
