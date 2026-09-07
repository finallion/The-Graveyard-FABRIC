package com.lion.graveyard.blockentities.renders;

import com.lion.graveyard.blockentities.SkeletalRemainsBlockEntity;
import com.lion.graveyard.blockentities.models.SkeletonPartsModel;
import com.lion.graveyard.blocks.SkeletalRemainsBlock;
import com.lion.graveyard.init.TGEntityModelLayers;
import com.lion.graveyard.pose.SkeletalPose;
import com.lion.graveyard.pose.SkeletalPoses;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

/// Draws bone remains by putting the parts where the pose says.
@Environment(EnvType.CLIENT)
public class SkeletalRemainsRenderer implements BlockEntityRenderer<SkeletalRemainsBlockEntity> {

    private static final Identifier SKELETON = new Identifier("textures/entity/skeleton/skeleton.png");
    private static final Identifier WITHER_SKELETON = new Identifier("textures/entity/skeleton/wither_skeleton.png");

    private final SkeletonPartsModel model;

    public SkeletalRemainsRenderer(BlockEntityRendererFactory.Context context) {
        this.model = new SkeletonPartsModel(context.getLayerModelPart(TGEntityModelLayers.SKELETON_PARTS_MODEL_LAYER));
    }

    @Override
    public void render(SkeletalRemainsBlockEntity remains, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        long seed = remains.poseSeed();
        SkeletalPose pose = SkeletalPoses.pick(seed);
        if (pose == null) {
            return;
        }

        BlockState state = remains.getCachedState();
        boolean wither = state.getBlock() instanceof SkeletalRemainsBlock block && block.isWither();
        VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(wither ? WITHER_SKELETON : SKELETON));

        matrices.push();
        matrices.translate(0.5D, 0.0D, 0.5D);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-state.get(SkeletalRemainsBlock.FACING).asRotation()));
        // Into the frame the parts are built in, where y counts downwards.
        matrices.scale(1.0F, -1.0F, -1.0F);

        int optional = 0;
        for (SkeletalPose.Placement placement : pose.placements()) {
            if (placement.optional() && SkeletalPoses.isMissing(seed, optional++)) {
                continue;
            }

            ModelPart part = this.model.part(placement.part());
            if (part == null) {
                continue;
            }

            part.setPivot(placement.x(), -placement.y(), placement.z());
            part.setAngles(radians(placement.pitch()), radians(placement.yaw()), radians(placement.roll()));
            part.xScale = placement.scale();
            part.yScale = placement.scale();
            part.zScale = placement.scale();
            part.render(matrices, consumer, light, overlay);
        }

        matrices.pop();
    }

    /// A pose reaching past its own block must not vanish when the block leaves the view.
    @Override
    public boolean rendersOutsideBoundingBox(SkeletalRemainsBlockEntity remains) {
        return true;
    }

    private static float radians(float degrees) {
        return degrees * ((float) Math.PI / 180.0F);
    }
}
