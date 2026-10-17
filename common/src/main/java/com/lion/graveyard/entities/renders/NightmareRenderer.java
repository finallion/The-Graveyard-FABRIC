package com.lion.graveyard.entities.renders;

import com.lion.graveyard.entities.NightmareEntity;
import com.lion.graveyard.entities.models.NightmareModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class NightmareRenderer extends GeoEntityRenderer<NightmareEntity> {

    public NightmareRenderer(EntityRendererProvider.Context context) {
        super(context, new NightmareModel());
        this.shadowRadius = 0.4F;
    }

    // stops the vanilla death animation
    @Override
    protected float getDeathMaxRotation(NightmareEntity entityLivingBaseIn) {
        return 0.0F;
    }




}
