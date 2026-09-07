package com.lion.graveyard.init;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.blockentities.models.SkeletonPartsModel;
import com.lion.graveyard.entities.models.CorruptedIllagerModel;
import com.lion.graveyard.platform.RegistryHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TGEntityModelLayers {

    public static final EntityModelLayer CORRUPTED_ILLAGER_MODEL_LAYER;
    public static final EntityModelLayer SKELETON_PARTS_MODEL_LAYER;

    static {
        CORRUPTED_ILLAGER_MODEL_LAYER = new EntityModelLayer(new Identifier(Graveyard.MOD_ID, "corrupted_illager"), "main");
        SKELETON_PARTS_MODEL_LAYER = new EntityModelLayer(new Identifier(Graveyard.MOD_ID, "skeleton_parts"), "main");
    }

    public static void init() {
        RegistryHelper.registerEntityModelLayer(CORRUPTED_ILLAGER_MODEL_LAYER, CorruptedIllagerModel::getTexturedModelData);
        RegistryHelper.registerEntityModelLayer(SKELETON_PARTS_MODEL_LAYER, SkeletonPartsModel::getTexturedModelData);
    }

}
