package com.lion.graveyard.entities.renders.features;

import java.util.function.Function;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.base.RenderPassInfo;
import software.bernie.geckolib.renderer.layer.builtin.TextureLayerGeoLayer;

public class GraveyardEyesLayer<T extends GeoAnimatable, R extends GeoRenderState> extends TextureLayerGeoLayer<T, Void, R> {
    private static final int FULL_LIGHT = 15728640;
    private static final int OPAQUE_WHITE = -1;

    private final Function<R, Identifier> eyesTexture;

    public GraveyardEyesLayer(GeoRenderer<T, Void, R> renderer, Identifier eyesTexture) {
        this(renderer, state -> eyesTexture);
    }

    public GraveyardEyesLayer(GeoRenderer<T, Void, R> renderer, Function<R, Identifier> eyesTexture) {
        super(renderer, MissingTextureAtlasSprite.getLocation(), RenderTypes::eyes);
        this.eyesTexture = eyesTexture;
    }

    @Override
    protected Identifier getTextureResource(R renderState) {
        return this.eyesTexture.apply(renderState);
    }

    @Override
    public void submitRenderTask(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        R renderState = renderPassInfo.renderState();
        int packedLight = renderPassInfo.packedLight();
        int packedOverlay = renderPassInfo.packedOverlay();
        int renderColor = renderPassInfo.renderColor();

        renderState.addGeckolibData(DataTickets.PACKED_LIGHT, FULL_LIGHT);
        renderState.addGeckolibData(DataTickets.PACKED_OVERLAY, OverlayTexture.NO_OVERLAY);
        renderState.addGeckolibData(DataTickets.RENDER_COLOR, OPAQUE_WHITE);
        super.submitRenderTask(renderPassInfo, renderTasks);
        renderState.addGeckolibData(DataTickets.PACKED_LIGHT, packedLight);
        renderState.addGeckolibData(DataTickets.PACKED_OVERLAY, packedOverlay);
        renderState.addGeckolibData(DataTickets.RENDER_COLOR, renderColor);
    }
}
