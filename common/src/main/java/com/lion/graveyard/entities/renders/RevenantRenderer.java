package com.lion.graveyard.entities.renders;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.RevenantEntity;
import com.lion.graveyard.entities.models.RevenantModel;
import com.lion.graveyard.entities.renders.features.GraveyardEyesLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class RevenantRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<RevenantEntity, R> {
    private static final Identifier EYES = Graveyard.id("textures/entity/revenant_eyes.png");

    public RevenantRenderer(EntityRendererProvider.Context context) {
        super(context, new RevenantModel());
        this.addRenderLayer(new GraveyardEyesLayer<>(this, EYES));
        this.shadowRadius = 0.4F;
    }

    // stops the vanilla death animation
    @Override
    protected float getDeathMaxRotation(GeoRenderState renderState) {
        return 0.0F;
    }

}
