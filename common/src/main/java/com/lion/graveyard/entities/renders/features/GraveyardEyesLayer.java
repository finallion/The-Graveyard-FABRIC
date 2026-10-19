package com.lion.graveyard.entities.renders.features;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Function;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.layer.TextureLayerGeoLayer;

public class GraveyardEyesLayer<T extends GeoAnimatable, R extends GeoRenderState> extends TextureLayerGeoLayer<T, Void, R> {
    private static final int FULL_LIGHT = 15728640;

    private final Function<R, ResourceLocation> eyesTexture;

    public GraveyardEyesLayer(GeoRenderer<T, Void, R> renderer, ResourceLocation eyesTexture) {
        this(renderer, state -> eyesTexture);
    }

    public GraveyardEyesLayer(GeoRenderer<T, Void, R> renderer, Function<R, ResourceLocation> eyesTexture) {
        super(renderer, MissingTextureAtlasSprite.getLocation(), RenderType::eyes);
        this.eyesTexture = eyesTexture;
    }

    @Override
    protected ResourceLocation getTextureResource(R renderState) {
        return this.eyesTexture.apply(renderState);
    }

    @Override
    public void render(
            R renderState,
            PoseStack poseStack,
            BakedGeoModel bakedModel,
            @Nullable RenderType renderType,
            MultiBufferSource bufferSource,
            @Nullable VertexConsumer buffer,
            int packedLight,
            int packedOverlay,
            int renderColor
    ) {
        RenderType eyesType = getRenderType(renderState);

        this.renderer.reRender(
                renderState,
                poseStack,
                bakedModel,
                bufferSource,
                eyesType,
                bufferSource.getBuffer(eyesType),
                FULL_LIGHT,
                OverlayTexture.NO_OVERLAY,
                -1
        );
    }
}
