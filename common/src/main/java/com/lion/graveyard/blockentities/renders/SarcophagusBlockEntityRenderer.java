package com.lion.graveyard.blockentities.renders;

import com.lion.graveyard.blockentities.SarcophagusBlockEntity;
import com.lion.graveyard.blockentities.enums.SarcophagusPart;
import com.lion.graveyard.blocks.SarcophagusBlock;
import com.lion.graveyard.init.TGBlockEntities;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public class SarcophagusBlockEntityRenderer<T extends BlockEntity & LidBlockEntity>
        implements BlockEntityRenderer<SarcophagusBlockEntity, SarcophagusBlockEntityRenderer.State> {
    private final ItemModelResolver itemModelResolver;

    public SarcophagusBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.itemModelResolver = ctx.itemModelResolver();
    }

    public static class State extends BlockEntityRenderState {
        public boolean headPart;
        public float openProgress;
        public Direction direction = Direction.NORTH;
        public final ItemStackRenderState base = new ItemStackRenderState();
        public final ItemStackRenderState lid = new ItemStackRenderState();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public int getViewDistance() {
        return 45;
    }

    public AABB getRenderBoundingBox(BlockEntity blockEntity) {
        return new AABB(blockEntity.getBlockPos())
                .inflate(1.0D)
                .expandTowards(0.0D, 1.0D, 0.0D);
    }

    @Override
    public void extractRenderState(
            SarcophagusBlockEntity entity,
            State state,
            float partialTick,
            Vec3 cameraPosition,
            ModelFeatureRenderer.CrumblingOverlay crumblingOverlay
    ) {
        BlockEntityRenderer.super.extractRenderState(entity, state, partialTick, cameraPosition, crumblingOverlay);
        BlockState blockState = entity.getBlockState();
        state.headPart = entity.getLevel() != null && blockState.getValue(SarcophagusBlock.PART) == SarcophagusPart.HEAD;

        if (!state.headPart) {
            return;
        }

        DoubleBlockCombiner.NeighborCombineResult<? extends SarcophagusBlockEntity> propertySource = DoubleBlockCombiner.combineWithNeigbour(TGBlockEntities.SARCOPHAGUS_BLOCK_ENTITY.get(), SarcophagusBlock::getSarcophagusPart, SarcophagusBlock::getOppositePartDirection, ChestBlock.FACING, blockState, entity.getLevel(), entity.getBlockPos(), (worldx, pos) -> false);
        float g = propertySource.apply(SarcophagusBlock.getAnimationProgressRetriever(entity)).get(partialTick);
        g = 1.0F - g;
        state.openProgress = 1.0F - g * g * g;
        state.direction = blockState.getValue(SarcophagusBlock.FACING).getOpposite();

        SarcophagusBlock block = (SarcophagusBlock) blockState.getBlock();
        updateModel(state.base, block.getBase().get(), entity);
        updateModel(state.lid, block.getLid().get(), entity);
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        if (state.headPart) {
            submitPart(state, poseStack, collector, state.lid, true);
            submitPart(state, poseStack, collector, state.base, false);
        }
    }

    private void submitPart(
            State state,
            PoseStack poseStack,
            SubmitNodeCollector collector,
            ItemStackRenderState model,
            boolean isLid
    ) {
        poseStack.pushPose();

        Direction direction = state.direction;
        float f = direction.toYRot();
        poseStack.mulPose(Axis.YP.rotationDegrees(-f));

        switch (direction) {
            case EAST -> poseStack.translate(-1.0F, 0F, 1.0F);
            case SOUTH -> poseStack.translate(0, 0F, 1.0F);
            case NORTH -> poseStack.translate(-1.0F, 0F, 0F);
        }

        if (isLid) {
            float g = state.openProgress;
            poseStack.mulPose(Axis.ZN.rotationDegrees(g * 45));
            poseStack.translate(g * 0.3, g * 0.3, 0.0F);
        }

        // Cancels the -0.5 offset that ItemTransform.apply adds.
        poseStack.translate(0.5F, 0.5F, 0.5F);
        model.submit(
                poseStack,
                GravestoneBlockEntityRenderer.CrumblingItemCollector.wrap(collector, state.breakProgress),
                state.lightCoords,
                OverlayTexture.NO_OVERLAY,
                0
        );

        poseStack.popPose();
    }

    private void updateModel(ItemStackRenderState state, Item item, SarcophagusBlockEntity entity) {
        state.clear();
        this.itemModelResolver.updateForTopItem(state, item.getDefaultInstance(), ItemDisplayContext.NONE, entity.getLevel(), null, 0);
    }
}
