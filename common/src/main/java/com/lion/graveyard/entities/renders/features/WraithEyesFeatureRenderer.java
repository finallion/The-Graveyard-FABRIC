package main.java.com.lion.graveyard.entities.renders.features;


import main.java.com.lion.graveyard.entities.WraithEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;



public class WraithEyesFeatureRenderer extends GeoRenderLayer<WraithEntity> {
    private final RenderLayer TEXTURE = RenderLayer.getEyes(new Identifier("graveyard:textures/entity/wraith_eyes.png"));
    private final GeoRenderer<WraithEntity> renderer;

    public WraithEyesFeatureRenderer(GeoRenderer<WraithEntity> entityRendererIn) {
        super(entityRendererIn);
        this.renderer = entityRendererIn;
    }

    @Override
    public void render(MatrixStack poseStack, WraithEntity animatable, BakedGeoModel bakedModel, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        VertexConsumer vertexConsumer = bufferSource.getBuffer(TEXTURE);
        renderer.actuallyRender(
                poseStack,
                animatable,
                bakedModel,
                renderType,
                bufferSource,
                vertexConsumer,
                true,
                partialTick,
                15728640,
                OverlayTexture.DEFAULT_UV,
                1.0F, 1.0F, 1.0F, 1.0F
        );
    }


}

