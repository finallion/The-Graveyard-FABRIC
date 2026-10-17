package com.lion.graveyard.init;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.models.CorruptedIllagerModel;
import com.lion.graveyard.platform.RegistryHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModelLayer;

@Environment(EnvType.CLIENT)
public class TGEntityModelLayers {

    public static final EntityModelLayer CORRUPTED_ILLAGER_MODEL_LAYER;

    static {
        CORRUPTED_ILLAGER_MODEL_LAYER = new EntityModelLayer(Graveyard.id("corrupted_illager"), "main");
    }

    public static void init() {
        RegistryHelper.registerEntityModelLayer(CORRUPTED_ILLAGER_MODEL_LAYER, CorruptedIllagerModel::getTexturedModelData);
    }

}
