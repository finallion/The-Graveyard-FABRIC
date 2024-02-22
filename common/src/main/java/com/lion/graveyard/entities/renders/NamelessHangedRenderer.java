package com.lion.graveyard.entities.renders;

import com.lion.graveyard.entities.NamelessHangedEntity;
import com.lion.graveyard.entities.models.NamelessHangedModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class NamelessHangedRenderer extends GeoEntityRenderer<NamelessHangedEntity> {

    public NamelessHangedRenderer(EntityRendererProvider.Context context) {
        super(context, new NamelessHangedModel());
        this.shadowRadius = 0.5F;
    }

    @Override
    public RenderType getRenderType(NamelessHangedEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }

}
