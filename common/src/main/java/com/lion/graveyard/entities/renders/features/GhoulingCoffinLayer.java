package com.lion.graveyard.entities.renders.features;

import com.lion.graveyard.client.GraveyardDataTickets;
import com.lion.graveyard.entities.GhoulingEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.function.BiConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.model.GeoBone;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.base.PerBoneRender;
import software.bernie.geckolib.renderer.base.RenderPassInfo;
import software.bernie.geckolib.renderer.layer.builtin.CustomBoneTextureGeoLayer;

/// Draws the torso translucent and the offhand item while the ghouling has its coffin.
public class GhoulingCoffinLayer<R extends GeoRenderState> extends CustomBoneTextureGeoLayer<GhoulingEntity, Void, R> {
    private static final String TORSO = "torso";

    public GhoulingCoffinLayer(GeoRenderer<GhoulingEntity, Void, R> renderer) {
        super(renderer, TORSO, MissingTextureAtlasSprite.getLocation());
    }

    @Override
    protected Identifier getTextureResource(R renderState) {
        return this.renderer.getTextureLocation(renderState);
    }

    @Override
    protected @Nullable RenderType getRenderType(R renderState, Identifier texture) {
        return RenderTypes.entityTranslucent(texture);
    }

    @Override
    public void preRender(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
        if (hasCoffin(renderPassInfo.renderState())) {
            super.preRender(renderPassInfo, renderTasks);
        }
    }

    @Override
    public void addPerBoneRender(RenderPassInfo<R> renderPassInfo, BiConsumer<GeoBone, PerBoneRender<R>> consumer) {
        if (!hasCoffin(renderPassInfo.renderState()) || !renderPassInfo.willRender()) {
            return;
        }

        renderPassInfo.model()
                .getBone(TORSO)
                .ifPresent(torso -> consumer.accept(torso, this::submitOffhandItem));
        super.addPerBoneRender(renderPassInfo, consumer);
    }

    private boolean hasCoffin(R renderState) {
        return renderState.getOrDefaultGeckolibData(GraveyardDataTickets.GHOULING_HAS_COFFIN, false);
    }

    private void submitOffhandItem(RenderPassInfo<R> renderPassInfo, GeoBone torso, SubmitNodeCollector renderTasks) {
        R renderState = renderPassInfo.renderState();
        ItemStack stack = renderState.getGeckolibData(GraveyardDataTickets.GHOULING_OFFHAND);
        PoseStack poseStack = renderPassInfo.poseStack();
        Minecraft minecraft = Minecraft.getInstance();
        ItemStackRenderState itemState = new ItemStackRenderState();
        int outlineColor = renderState instanceof EntityRenderState entityState ? entityState.outlineColor : 0;

        minecraft.getItemModelResolver()
                .updateForTopItem(itemState, stack, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, minecraft.level, null, 0);
        torso.translateAwayFromPivotPoint(poseStack);
        poseStack.mulPose(Axis.XP.rotationDegrees(90));
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.mulPose(Axis.ZP.rotationDegrees(-25));
        poseStack.translate(1.3D, 0.86D, 0.0D);
        poseStack.scale(2.0F, 2.0F, 2.0F);
        itemState.submit(poseStack, renderTasks, renderPassInfo.packedLight(), renderPassInfo.packedOverlay(), outlineColor);
    }
}
