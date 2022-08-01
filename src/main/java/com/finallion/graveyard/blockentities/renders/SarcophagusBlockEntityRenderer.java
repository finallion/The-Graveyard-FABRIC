package com.finallion.graveyard.blockentities.renders;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.blockentities.SarcophagusBlockEntity;
import com.finallion.graveyard.blockentities.enums.SarcophagusPart;
import com.finallion.graveyard.blocks.GravestoneBlock;
import com.finallion.graveyard.blocks.SarcophagusBlock;
import com.finallion.graveyard.init.TGBlocks;
import com.finallion.graveyard.init.TGItems;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.BakedModelManagerHelper;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LidOpenable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.LightmapCoordinatesRetriever;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

import java.util.*;

@Environment(EnvType.CLIENT)
//TODO: SPLIT IN TWO CLASSES: COFFIN AND SARCOPHAGUS
// 1.19 required to change this class. Placing the block would stop particle spawning of other blocks in the world. Don't have any idea why.
// So instead of rendering the block, this renders the item, split into two to allow for the lid animation
public class SarcophagusBlockEntityRenderer<T extends BlockEntity & LidOpenable> implements BlockEntityRenderer<SarcophagusBlockEntity> {

    public SarcophagusBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(SarcophagusBlockEntity entity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        BlockState blockState = entity.getCachedState();
        DoubleBlockProperties.PropertySource<? extends SarcophagusBlockEntity> propertySource = DoubleBlockProperties.toPropertySource(TGBlocks.SARCOPHAGUS_BLOCK_ENTITY, SarcophagusBlock::getSarcophagusPart, SarcophagusBlock::getOppositePartDirection, ChestBlock.FACING, blockState, entity.getWorld(), entity.getPos(), (worldx, pos) -> {
            return false;
        });
        float g = ((Float2FloatFunction) propertySource.apply(SarcophagusBlock.getAnimationProgressRetriever((LidOpenable) entity))).get(tickDelta);
        g = 1.0F - g;
        g = 1.0F - g * g * g;

        ItemStack stack = entity.getCachedState().getBlock().asItem().getDefaultStack();
        boolean isCoffin = entity.isCoffin();

        if (entity.getWorld() != null && entity.getCachedState().get(SarcophagusBlock.PART) == SarcophagusPart.HEAD) {
            if (isCoffin) {
                renderCoffinLid(entity, stack, matrixStack, vertexConsumers, light, g);
                renderCoffinBase(entity, stack, matrixStack, vertexConsumers, light);
            } else {
                renderLid(entity, stack, matrixStack, vertexConsumers, light, g);
                renderBase(entity, stack, matrixStack, vertexConsumers, light);
            }
        }
    }
    private void renderCoffinBase(SarcophagusBlockEntity entity, ItemStack stack, MatrixStack matrixStack, VertexConsumerProvider vertexConsumer, int light) {
        BakedModel base = getCoffinModel(entity);

        matrixStack.push();
        //matrixStack.scale(4.5828F, 4.5F, 4.269F);
        Direction direction = entity.getCachedState().get(SarcophagusBlock.FACING).getOpposite();
        float f = direction.asRotation();

        if (direction == Direction.EAST) {
            matrixStack.scale(4.269F, 4.5F, 4.5826F);
            matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-f));
            matrixStack.translate(0, 0, 0.23435);
        } else if (direction == Direction.WEST) {
            matrixStack.scale(4.269F, 4.5F, 4.5826F);
            matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-f));
            matrixStack.translate(0.2185F, 0, 0);
        } else if (direction == Direction.SOUTH) {
            matrixStack.scale(4.5826F, 4.5F, 4.269F);
            matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-f));
            matrixStack.translate(0.2185F, 0, 0.23435);
        } else {
            matrixStack.scale(4.5826F, 4.5F, 4.269F);
            matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-f));
        }



        matrixStack.translate(-0.10925, 0.0625F, 0.0155);

        MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.GROUND, false, matrixStack, vertexConsumer, light, OverlayTexture.DEFAULT_UV, base);

        matrixStack.pop();

    }
    private void renderCoffinLid(SarcophagusBlockEntity entity, ItemStack stack, MatrixStack matrixStack, VertexConsumerProvider vertexConsumer, int light, float g) {
        BakedModel lid = getCoffinLidModel(entity);

        matrixStack.push();
        //matrixStack.scale(4.5828F, 4.5F, 4.269F);
        Direction direction = entity.getCachedState().get(SarcophagusBlock.FACING).getOpposite();
        float f = direction.asRotation();

        //matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-f));
        if (direction == Direction.EAST) {
            matrixStack.scale(4.269F, 4.5F, 4.5826F);
            matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-f));
            matrixStack.translate(0, 0, 0.23435);
        } else if (direction == Direction.WEST) {
            matrixStack.scale(4.269F, 4.5F, 4.5826F);
            matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-f));
            matrixStack.translate(0.2185F, 0, 0);
        } else if (direction == Direction.SOUTH) {
            matrixStack.scale(4.5826F, 4.5F, 4.269F);
            matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-f));
            matrixStack.translate(0.2185F, 0, 0.23435);
        } else {
            matrixStack.scale(4.5826F, 4.5F, 4.269F);
            matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-f));
        }
        matrixStack.translate(-0.10925, 0.0625F, 0.0155);

        // ANIMATION START
        matrixStack.multiply(Vec3f.NEGATIVE_Z.getDegreesQuaternion(g * 70)); // lid rotation
        // ANIMATION END

        MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.GROUND, false, matrixStack, vertexConsumer, light, OverlayTexture.DEFAULT_UV, lid);

        matrixStack.pop();
    }

    private void renderBase(SarcophagusBlockEntity entity, ItemStack stack, MatrixStack matrixStack, VertexConsumerProvider vertexConsumer, int light) {
        BakedModel base = MinecraftClient.getInstance().getItemRenderer().getModel(TGItems.SARCOPHAGUS_BASE.getDefaultStack(), null, null, 0);

        matrixStack.push();
        matrixStack.scale(5.0F, 5.0F, 5.0F);
        Direction direction = entity.getCachedState().get(SarcophagusBlock.FACING).getOpposite();
        float f = direction.asRotation();

        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-f));
        if (direction == Direction.EAST) {
            matrixStack.translate(0, 0, 0.2F);
        } else if (direction == Direction.WEST) {
            matrixStack.translate(0.2F, 0, 0);
        } else if (direction == Direction.SOUTH) {
            matrixStack.translate(0.2F, 0, 0.2F);
        }

        matrixStack.translate(-0.1F, 0.0375F, 0.05615F);

        MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.GROUND, false, matrixStack, vertexConsumer, light, OverlayTexture.DEFAULT_UV, base);

        matrixStack.pop();
    }

    private void renderLid(SarcophagusBlockEntity entity, ItemStack stack, MatrixStack matrixStack, VertexConsumerProvider vertexConsumer, int light, float g) {
        BakedModel lid = MinecraftClient.getInstance().getItemRenderer().getModel(TGItems.SARCOPHAGUS_LID.getDefaultStack(), null, null, 0);

        matrixStack.push();
        matrixStack.scale(5.0F, 5.0F, 5.0F);
        Direction direction = entity.getCachedState().get(SarcophagusBlock.FACING).getOpposite();
        float f = direction.asRotation();

        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-f));
        if (direction == Direction.EAST) {
            matrixStack.translate(0, 0, 0.2F);
        } else if (direction == Direction.WEST) {
            matrixStack.translate(0.2F, 0, 0);
        } else if (direction == Direction.SOUTH) {
            matrixStack.translate(0.2F, 0, 0.2F);
        }

        matrixStack.translate(-0.1F, 0.0375F, 0.05615F);

        // ANIMATION START
        matrixStack.multiply(Vec3f.NEGATIVE_Z.getDegreesQuaternion(g * 70)); // lid rotation
        // ANIMATION END


        MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.GROUND, false, matrixStack, vertexConsumer, light, OverlayTexture.DEFAULT_UV, lid);

        matrixStack.pop();
    }

    // TODO: cleaner way
    private BakedModel getCoffinModel(SarcophagusBlockEntity entity) {
        String wood = entity.getCachedState().toString().split("_coffin")[0];
        if (wood.contains("dark_oak")) {
            return MinecraftClient.getInstance().getItemRenderer().getModel(TGItems.DARK_OAK_COFFIN_BASE.getDefaultStack(), null, null, 0);
        } else if (wood.contains("oak")) {
            return MinecraftClient.getInstance().getItemRenderer().getModel(TGItems.OAK_COFFIN_BASE.getDefaultStack(), null, null, 0);
        } else if (wood.contains("acacia")) {
            return MinecraftClient.getInstance().getItemRenderer().getModel(TGItems.ACACIA_COFFIN_BASE.getDefaultStack(), null, null, 0);
        } else if (wood.contains("jungle")) {
            return MinecraftClient.getInstance().getItemRenderer().getModel(TGItems.JUNGLE_COFFIN_BASE.getDefaultStack(), null, null, 0);
        } else if (wood.contains("warped")) {
            return MinecraftClient.getInstance().getItemRenderer().getModel(TGItems.WARPED_COFFIN_BASE.getDefaultStack(), null, null, 0);
        } else if (wood.contains("crimson")) {
            return MinecraftClient.getInstance().getItemRenderer().getModel(TGItems.CRIMSON_COFFIN_BASE.getDefaultStack(), null, null, 0);
        } else if (wood.contains("spruce")) {
            return MinecraftClient.getInstance().getItemRenderer().getModel(TGItems.SPRUCE_COFFIN_BASE.getDefaultStack(), null, null, 0);
        } else {
            return MinecraftClient.getInstance().getItemRenderer().getModel(TGItems.BIRCH_COFFIN_BASE.getDefaultStack(), null, null, 0);
        }
    }

    private BakedModel getCoffinLidModel(SarcophagusBlockEntity entity) {
        String wood = entity.getCachedState().toString().split("_coffin")[0];
        if (wood.contains("dark_oak")) {
            return MinecraftClient.getInstance().getItemRenderer().getModel(TGItems.DARK_OAK_COFFIN_LID.getDefaultStack(), null, null, 0);
        } else if (wood.contains("oak")) {
            return MinecraftClient.getInstance().getItemRenderer().getModel(TGItems.OAK_COFFIN_LID.getDefaultStack(), null, null, 0);
        } else if (wood.contains("acacia")) {
            return MinecraftClient.getInstance().getItemRenderer().getModel(TGItems.ACACIA_COFFIN_LID.getDefaultStack(), null, null, 0);
        } else if (wood.contains("jungle")) {
            return MinecraftClient.getInstance().getItemRenderer().getModel(TGItems.JUNGLE_COFFIN_LID.getDefaultStack(), null, null, 0);
        } else if (wood.contains("warped")) {
            return MinecraftClient.getInstance().getItemRenderer().getModel(TGItems.WARPED_COFFIN_LID.getDefaultStack(), null, null, 0);
        } else if (wood.contains("crimson")) {
            return MinecraftClient.getInstance().getItemRenderer().getModel(TGItems.CRIMSON_COFFIN_LID.getDefaultStack(), null, null, 0);
        } else if (wood.contains("spruce")) {
            return MinecraftClient.getInstance().getItemRenderer().getModel(TGItems.SPRUCE_COFFIN_LID.getDefaultStack(), null, null, 0);
        } else {
            return MinecraftClient.getInstance().getItemRenderer().getModel(TGItems.BIRCH_COFFIN_LID.getDefaultStack(), null, null, 0);
        }
    }


}


