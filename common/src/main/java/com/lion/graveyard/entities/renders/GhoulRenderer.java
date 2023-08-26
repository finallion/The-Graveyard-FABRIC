package main.java.com.lion.graveyard.entities.renders;

import main.java.com.lion.graveyard.entities.GhoulEntity;
import main.java.com.lion.graveyard.entities.models.BaseGhoulModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GhoulRenderer extends GeoEntityRenderer<GhoulEntity> {

    public GhoulRenderer(EntityRendererFactory.Context context) {
        super(context, new BaseGhoulModel());
        // glowing eyes
        //this.addLayer(new GhoulEyesFeatureRenderer(this));
        this.shadowRadius = 0.7F;
    }



    // stops the vanilla death animation
    @Override
    protected float getDeathMaxRotation(GhoulEntity entityLivingBaseIn) {
        return 0.0F;
    }

}
