package com.lion.graveyard.entities.renders;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.WraithEntity;
import com.lion.graveyard.entities.models.WraithModel;
import com.lion.graveyard.entities.renders.features.GraveyardEyesLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class WraithRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<WraithEntity, R> {
    private static final Identifier EYES = Graveyard.id("textures/entity/wraith_eyes.png");

    public WraithRenderer(EntityRendererProvider.Context context) {
        super(context, new WraithModel());
        this.addRenderLayer(new GraveyardEyesLayer<>(this, EYES));
        this.shadowRadius = 0.3F;
    }

    // stops the vanilla death animation
    @Override
    protected float getDeathMaxRotation(GeoRenderState renderState) {
        return 0.0F;
    }

    @Override
    public RenderType getRenderType(R renderState, Identifier texture) {
        return RenderTypes.entityTranslucent(texture);
    }

}
