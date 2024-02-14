package com.lion.graveyard.init;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.models.AcolyteModel;
import com.lion.graveyard.entities.models.CorruptedIllagerModel;
import com.lion.graveyard.entities.renders.features.IllagerArmorFeatureRenderer;
import com.lion.graveyard.platform.RegistryHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class TGEntityModelLayers {

    public static final ModelLayerLocation CORRUPTED_ILLAGER_MODEL_LAYER;
    public static final ModelLayerLocation ACOLYTE_MODEL_LAYER;
    public static final ModelLayerLocation CORRUPTED_ILLAGER_INNER_ARMOR_MODEL_LAYER;
    public static final ModelLayerLocation CORRUPTED_ILLAGER_OUTER_ARMOR_MODEL_LAYER;

    static {
        CORRUPTED_ILLAGER_MODEL_LAYER = new ModelLayerLocation(new ResourceLocation(Graveyard.MOD_ID, "corrupted_illager"), "main");
        ACOLYTE_MODEL_LAYER = new ModelLayerLocation(new ResourceLocation(Graveyard.MOD_ID, "acolyte"), "acolyte");
        CORRUPTED_ILLAGER_INNER_ARMOR_MODEL_LAYER = new ModelLayerLocation(new ResourceLocation(Graveyard.MOD_ID, "corrupted_illager_inner_armor"), "inner_armor");
        CORRUPTED_ILLAGER_OUTER_ARMOR_MODEL_LAYER = new ModelLayerLocation(new ResourceLocation(Graveyard.MOD_ID, "corrupted_illager_outer_armor"), "outer_armor");
    }

    public static void init() {
        RegistryHelper.registerEntityModelLayer(CORRUPTED_ILLAGER_MODEL_LAYER, CorruptedIllagerModel::getTexturedModelData);
        RegistryHelper.registerEntityModelLayer(ACOLYTE_MODEL_LAYER, AcolyteModel::getTexturedModelData);
        RegistryHelper.registerEntityModelLayer(CORRUPTED_ILLAGER_INNER_ARMOR_MODEL_LAYER, IllagerArmorFeatureRenderer::createInnerArmorLayer);
        RegistryHelper.registerEntityModelLayer(CORRUPTED_ILLAGER_OUTER_ARMOR_MODEL_LAYER, IllagerArmorFeatureRenderer::createOuterArmorLayer);
    }

}
