package com.lion.graveyard.entities.renders;

import com.lion.graveyard.entities.FallingCorpse;
import com.lion.graveyard.entities.models.FallingCorpseModel;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FallingCorpseRenderer extends GeoEntityRenderer<FallingCorpse> {

    public FallingCorpseRenderer(EntityRendererFactory.Context context) {
        super(context, new FallingCorpseModel());
        this.shadowRadius = 0.4F;
    }

    @Override
    protected void applyRotations(FallingCorpse entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
        rotationYaw = entityLiving.getRotation();
        super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
    }

    @Override
    public RenderLayer getRenderType(FallingCorpse animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return RenderLayer.getEntityCutoutNoCull(texture);
    }


}
