package com.lion.graveyard.entities.renders.features;

import com.lion.graveyard.client.GraveyardDataTickets;
import com.lion.graveyard.entities.CorvidEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import com.mojang.math.Axis;
import java.util.List;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.model.BakedGeoModel;
import software.bernie.geckolib.cache.model.GeoBone;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.layer.builtin.BlockAndItemGeoLayer;

public class CorvidHeldItemLayer<R extends GeoRenderState> extends BlockAndItemGeoLayer<CorvidEntity, Void, R> {

    public CorvidHeldItemLayer(GeoRenderer<CorvidEntity, Void, R> renderer) {
        super(renderer);
    }

    @Override
    protected List<RenderData<R>> getRelevantBones(R renderState, BakedGeoModel model) {
        return List.of(new RenderData<>("head", ItemDisplayContext.GROUND, (bone, state) -> Either.left(state.getGeckolibData(GraveyardDataTickets.CORVID_HELD_ITEM))));
    }

    @Override
    public void addRenderData(CorvidEntity animatable, @Nullable Void relatedObject, R renderState, float partialTick) {
        renderState.addGeckolibData(GraveyardDataTickets.CORVID_HELD_ITEM, animatable.getItemBySlot(EquipmentSlot.MAINHAND).copy());
    }

    @Override
    protected void submitItemStackRender(
            PoseStack poseStack,
            GeoBone bone,
            ItemStack stack,
            ItemDisplayContext displayContext,
            R renderState,
            SubmitNodeCollector renderTasks,
            CameraRenderState cameraState,
            int packedLight,
            int packedOverlay,
            int renderColor
    ) {
        // Beak tip: head pivot at z=-6, beak end at z=-9, two units above the pivot.
        poseStack.translate(0.0F, 0.09F, -0.24F);
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        poseStack.scale(0.6F, 0.6F, 0.6F);
        super.submitItemStackRender(poseStack, bone, stack, displayContext, renderState, renderTasks, cameraState, packedLight, packedOverlay, renderColor);
    }
}
