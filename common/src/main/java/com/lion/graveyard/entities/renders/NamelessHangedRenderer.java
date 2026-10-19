package com.lion.graveyard.entities.renders;

import com.lion.graveyard.entities.NamelessHangedEntity;
import com.lion.graveyard.entities.models.NamelessHangedModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class NamelessHangedRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<NamelessHangedEntity, R> {

    public NamelessHangedRenderer(EntityRendererProvider.Context context) {
        super(context, new NamelessHangedModel());
        this.shadowRadius = 0.5F;
    }

    @Override
    public RenderType getRenderType(R renderState, ResourceLocation texture) {
        return RenderType.entityTranslucent(texture);
    }

}
