package com.lion.graveyard.entities.renders.features;

import com.lion.graveyard.client.GraveyardDataTickets;
import com.lion.graveyard.entities.CorvidEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.function.BiConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.base.PerBoneRender;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.RenderUtil;

/// `BlockAndItemGeoLayer` applies the bone rotation twice, so this layer does the bone transform itself.
public class CorvidHeldItemLayer<R extends GeoRenderState> extends GeoRenderLayer<CorvidEntity, Void, R> {

    public CorvidHeldItemLayer(GeoRenderer<CorvidEntity, Void, R> renderer) {
        super(renderer);
    }

    @Override
    public void addRenderData(CorvidEntity animatable, Void relatedObject, R renderState) {
        renderState.addGeckolibData(GraveyardDataTickets.CORVID_HELD_ITEM, animatable.getItemBySlot(EquipmentSlot.MAINHAND).copy());
    }

    @Override
    public void addPerBoneRender(R renderState, BakedGeoModel model, BiConsumer<GeoBone, PerBoneRender<R>> consumer) {
        if (renderState.getGeckolibData(GraveyardDataTickets.CORVID_HELD_ITEM).isEmpty()) {
            return;
        }

        if (renderState.getOrDefaultGeckolibData(DataTickets.INVISIBLE_TO_PLAYER, false)) {
            return;
        }

        if (this.renderer.getRenderType(renderState, this.renderer.getTextureLocation(renderState)) == null) {
            return;
        }

        model.getBone("head").ifPresent(head -> consumer.accept(head, this::renderHeldItem));
    }

    private void renderHeldItem(
            R renderState,
            PoseStack poseStack,
            GeoBone bone,
            @Nullable RenderType renderType,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay,
            int renderColor
    ) {
        ItemStack stack = renderState.getGeckolibData(GraveyardDataTickets.CORVID_HELD_ITEM);

        RenderUtil.translateToPivotPoint(poseStack, bone);
        // Beak tip: head pivot at z=-6, beak end at z=-9, two units above the pivot.
        poseStack.translate(0.0F, 0.09F, -0.24F);
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        poseStack.scale(0.6F, 0.6F, 0.6F);
        Minecraft.getInstance().getItemRenderer().renderStatic(
                stack,
                ItemDisplayContext.GROUND,
                packedLight,
                packedOverlay,
                poseStack,
                bufferSource,
                Minecraft.getInstance().level,
                renderState.getGeckolibData(DataTickets.ANIMATABLE_INSTANCE_ID).intValue()
        );
    }
}
