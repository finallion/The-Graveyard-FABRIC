package main.java.com.lion.graveyard.entities.renders;

import main.java.com.lion.graveyard.entities.WraithEntity;
import main.java.com.lion.graveyard.entities.models.WraithModel;
import main.java.com.lion.graveyard.entities.renders.features.WraithEyesFeatureRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WraithRenderer extends GeoEntityRenderer<WraithEntity> {

    public WraithRenderer(EntityRendererFactory.Context context) {
        super(context, new WraithModel());
        this.addRenderLayer(new WraithEyesFeatureRenderer(this));
        this.shadowRadius = 0.3F;
    }

    // stops the vanilla death animation
    @Override
    protected float getDeathMaxRotation(WraithEntity entityLivingBaseIn) {
        return 0.0F;
    }

    @Override
    public RenderLayer getRenderType(WraithEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return RenderLayer.getEntityTranslucent(texture);
    }

}
