package com.finallion.graveyard.blockentities.model;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.blockentities.CoffinBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import java.util.Locale;

public class CoffinModel extends AnimatedGeoModel<CoffinBlockEntity> {

    @Override
    public Identifier getAnimationFileLocation(CoffinBlockEntity animatable) {
        if (animatable.getCachedState().getBlock().toString().contains("sarcophagus")) {
            return new Identifier(TheGraveyard.MOD_ID, "animations/sarcophagus/sarcophagus.animation.json");
        } else {
            return new Identifier(TheGraveyard.MOD_ID, "animations/coffin/coffin.animation.json");
        }
    }

    @Override
    public Identifier getModelLocation(CoffinBlockEntity animatable) {
        if (animatable.getCachedState().getBlock().toString().contains("sarcophagus")) {
            return new Identifier(TheGraveyard.MOD_ID, "geo/sarcophagus.geo.json");
        } else {
            return new Identifier(TheGraveyard.MOD_ID, "geo/coffin.geo.json");
        }
    }

    @Override
    public Identifier getTextureLocation(CoffinBlockEntity entity) {
        String name = entity.getCachedState().getBlock().toString();
        if (name.contains("sarcophagus")) {
            return new Identifier(TheGraveyard.MOD_ID, "textures/block/sarcophagus.png");
        } else {
            if (name.contains("dark_oak")) {
                return new Identifier(TheGraveyard.MOD_ID, "textures/block/dark_oak_coffin.png");
            } else if (name.contains("oak")) {
                return new Identifier(TheGraveyard.MOD_ID, "textures/block/oak_coffin.png");
            } else if (name.contains("spruce")) {
                return new Identifier(TheGraveyard.MOD_ID, "textures/block/spruce_coffin.png");
            } else if (name.contains("birch")) {
                return new Identifier(TheGraveyard.MOD_ID, "textures/block/birch_coffin.png");
            } else if (name.contains("jungle")) {
                return new Identifier(TheGraveyard.MOD_ID, "textures/block/jungle_coffin.png");
            } else {
                return new Identifier(TheGraveyard.MOD_ID, "textures/block/acacia_coffin.png");
            }
        }
    }
}