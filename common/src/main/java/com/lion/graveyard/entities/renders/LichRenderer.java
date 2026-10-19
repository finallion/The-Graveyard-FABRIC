package com.lion.graveyard.entities.renders;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.LichEntity;
import com.lion.graveyard.entities.models.LichModel;
import com.lion.graveyard.entities.renders.features.GraveyardEyesLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class LichRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<LichEntity, R> {
    private static final Identifier EYES = Graveyard.id("textures/entity/lich_eye_texture.png");

    public LichRenderer(EntityRendererProvider.Context context) {
        super(context, new LichModel());
        this.shadowRadius = 1.0F;
        this.addRenderLayer(new GraveyardEyesLayer<>(this, EYES));
    }

    // stops the vanilla death animation
    @Override
    protected float getDeathMaxRotation(GeoRenderState renderState) {
        return 0.0F;
    }

    @Override
    public RenderType getRenderType(R renderState, Identifier texture) {
        return RenderTypes.entityCutoutNoCull(texture);
    }

}
