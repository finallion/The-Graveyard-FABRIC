package com.finallion.graveyard.entities.models;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.entities.GhoulEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BaseGhoulModel extends AnimatedGeoModel<GhoulEntity> {
    private Identifier texture = new Identifier(TheGraveyard.MOD_ID, "textures/entity/ghoul_skin1.png");

    @Override
    public Identifier getModelLocation(GhoulEntity object) {
        return new Identifier(TheGraveyard.MOD_ID, "geo/ghoul.geo.json");
    }

    @Override
    public Identifier getTextureLocation(GhoulEntity object) {
        return texture;
    }

    @Override
    public Identifier getAnimationFileLocation(GhoulEntity animatable) {
        return new Identifier(TheGraveyard.MOD_ID, "animations/ghoul/ghoul.animation.json");
    }

    @Override
    public void setLivingAnimations(GhoulEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);

        switch (entity.getVariant()) {
            //case 0 -> texture = new Identifier(TheGraveyard.MOD_ID, "textures/entity/ghoul_skin1.png");
            case 1 -> texture = new Identifier(TheGraveyard.MOD_ID, "textures/entity/ghoul_skin2.png");
            case 2 -> texture = new Identifier(TheGraveyard.MOD_ID, "textures/entity/ghoul_skin3.png");
            case 3 -> texture = new Identifier(TheGraveyard.MOD_ID, "textures/entity/ghoul_skin4.png");
            case 4 -> texture = new Identifier(TheGraveyard.MOD_ID, "textures/entity/ghoul_skin5.png");
            case 5 -> texture = new Identifier(TheGraveyard.MOD_ID, "textures/entity/ghoul_skin6.png");
            case 6 -> texture = new Identifier(TheGraveyard.MOD_ID, "textures/entity/ghoul_skin7.png");
            case 7 -> texture = new Identifier(TheGraveyard.MOD_ID, "textures/entity/ghoul_skin8.png");
            default -> texture = new Identifier(TheGraveyard.MOD_ID, "textures/entity/ghoul_skin1.png");
        };
    }
}
