package main.java.com.lion.graveyard.entities.models;

import com.finallion.graveyard.TheGraveyard;
import main.java.com.lion.graveyard.entities.ReaperEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class ReaperModel extends GeoModel<ReaperEntity> {

    @Override
    public Identifier getModelResource(ReaperEntity object) {
        return new Identifier(TheGraveyard.MOD_ID, "geo/reaper.geo.json");
    }

    @Override
    public Identifier getTextureResource(ReaperEntity object) {
        return new Identifier(TheGraveyard.MOD_ID, "textures/entity/reaper.png");
    }

    @Override
    public Identifier getAnimationResource(ReaperEntity animatable) {
        return new Identifier(TheGraveyard.MOD_ID, "animations/reaper/reaper.animation.json");
    }


}
