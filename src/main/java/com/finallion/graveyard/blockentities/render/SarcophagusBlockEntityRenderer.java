package com.finallion.graveyard.blockentities.render;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.blockentities.SarcophagusBlockEntity;
import com.finallion.graveyard.blockentities.enums.SarcophagusPart;
import com.finallion.graveyard.blocks.SarcophagusBlock;
import com.finallion.graveyard.init.TGBlocks;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.BakedModelManagerHelper;
import net.minecraft.block.*;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.BedPart;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.block.ChestAnimationProgress;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.LightmapCoordinatesRetriever;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

import java.util.Random;

@Environment(EnvType.CLIENT)
public class SarcophagusBlockEntityRenderer implements BlockEntityRenderer<SarcophagusBlockEntity> {
    public static final Identifier SARCOPHAGUS_FOOT = new Identifier(TheGraveyard.MOD_ID, "block/sarcophagus_foot");
    public static final Identifier SARCOPHAGUS_HEAD = new Identifier(TheGraveyard.MOD_ID, "block/sarcophagus_head");
    private final BakedModel sarcophagusModelHead;
    private final BakedModel sarcophagusModelFoot;

    public SarcophagusBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        MinecraftClient client = MinecraftClient.getInstance();
        this.sarcophagusModelHead = BakedModelManagerHelper.getModel(client.getBakedModelManager(), SARCOPHAGUS_HEAD);
        this.sarcophagusModelFoot = BakedModelManagerHelper.getModel(client.getBakedModelManager(), SARCOPHAGUS_FOOT);
    }


    @Override
    public void render(SarcophagusBlockEntity entity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        World world = entity.getWorld();

        BlockState blockState = entity.getCachedState();
        DoubleBlockProperties.PropertySource<? extends SarcophagusBlockEntity> propertySource = DoubleBlockProperties.toPropertySource(TGBlocks.SARCOPHAGUS_BLOCK_ENTITY, SarcophagusBlock::getSarcophagusPart, SarcophagusBlock::getOppositePartDirection, ChestBlock.FACING, blockState, world, entity.getPos(), (worldx, pos) -> {
            return false;
        });
        float g = ((Float2FloatFunction)propertySource.apply(SarcophagusBlock.getAnimationProgressRetriever((ChestAnimationProgress)entity))).get(tickDelta);
        g = 1.0F - g;
        g = 1.0F - g * g * g;
        int k = ((Int2IntFunction)propertySource.apply(new LightmapCoordinatesRetriever())).get(light);

        if (world != null) {
            this.renderPart(entity, matrixStack, vertexConsumers, blockState.get(SarcophagusBlock.PART) == SarcophagusPart.HEAD ? this.sarcophagusModelHead : this.sarcophagusModelFoot, (Direction)blockState.get(SarcophagusBlock.FACING), vertexConsumers.getBuffer(RenderLayers.getBlockLayer(entity.getCachedState())), k, overlay, false, g);
        } else {
            this.renderPart(entity, matrixStack, vertexConsumers, sarcophagusModelFoot, Direction.SOUTH, vertexConsumers.getBuffer(RenderLayers.getBlockLayer(entity.getCachedState())), k, overlay, true, g);
            this.renderPart(entity, matrixStack, vertexConsumers, sarcophagusModelHead, Direction.SOUTH, vertexConsumers.getBuffer(RenderLayers.getBlockLayer(entity.getCachedState())), k, overlay, false, g);
        }


    }


    private void renderPart(SarcophagusBlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, BakedModel model, Direction direction, VertexConsumer vertexConsumer, int light, int overlay, boolean isFoot, float openFactor) {
        BlockModelRenderer renderer = MinecraftClient.getInstance().getBlockRenderManager().getModelRenderer();
        World world = entity.getWorld();

        matrices.push();
        matrices.translate(0.0D, 0.0D, isFoot ? -1.0D : 0.0D);

        float f = ((Direction)entity.getCachedState().get(SarcophagusBlock.FACING)).getOpposite().asRotation();
        matrices.translate(0.5D, 0.5D, 0.5D);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-f));


        //matrices.translate(0.5D, 0.5D, 0.5D);
        //float rotationFoot = -entity.getCachedState().get(SarcophagusBlock.FACING).getOpposite().asRotation();
        //float rotationHead = -entity.getCachedState().get(SarcophagusBlock.FACING).asRotation();
        //matrices.multiply(isFoot ? Vec3f.POSITIVE_Y.getDegreesQuaternion(rotationHead) : Vec3f.POSITIVE_Y.getDegreesQuaternion(rotationFoot));

        matrices.translate(-0.5D, -0.5D, -0.5D);
        renderer.render(world, model, entity.getCachedState(), entity.getPos(), matrices, vertexConsumer, false, new Random(), 0, overlay);


        matrices.pop();
    }
}

