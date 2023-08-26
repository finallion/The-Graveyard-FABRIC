package main.java.com.lion.graveyard.entities.renders;

import main.java.com.lion.graveyard.entities.NightmareEntity;
import main.java.com.lion.graveyard.entities.models.NightmareModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class NightmareRenderer extends GeoEntityRenderer<NightmareEntity> {

    public NightmareRenderer(EntityRendererFactory.Context context) {
        super(context, new NightmareModel());
        this.shadowRadius = 0.4F;
    }

    // stops the vanilla death animation
    @Override
    protected float getDeathMaxRotation(NightmareEntity entityLivingBaseIn) {
        return 0.0F;
    }




}
