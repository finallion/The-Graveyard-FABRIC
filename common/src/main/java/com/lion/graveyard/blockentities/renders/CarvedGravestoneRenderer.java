package com.lion.graveyard.blockentities.renders;

import com.lion.graveyard.blocks.CarvedGravestoneBlock;
import com.lion.graveyard.init.TGBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/// Draws the gravestone geometry with the texture of the block it was carved from. Works because
/// every gravestone model is UV-mapped against a plain 16x16 block texture already, so only the
/// source sprite has to be swapped for the material's.
///
/// Shared by the world renderer and the per-loader item renderers, which is why the transformation
/// is left to the caller: in the world it has to be applied by hand, while the item renderer has
/// already applied it before handing over.
@Environment(EnvType.CLIENT)
public final class CarvedGravestoneRenderer {

    /// Ints per vertex in a baked quad: position (3), colour, uv (2), light, normal.
    private static final int VERTEX_STRIDE = 8;

    private CarvedGravestoneRenderer() {
    }

    /// Geometry comes from the default state, so the blockstate rotation stays out of it - every
    /// caller rotates itself.
    public static BakedModel geometry() {
        return geometry(TGBlocks.CARVED_GRAVESTONE.get());
    }

    public static BakedModel geometry(Block block) {
        return MinecraftClient.getInstance().getBlockRenderManager().getModel(block.getDefaultState());
    }

    public static Sprite sprite(@Nullable Block material) {
        BlockState state = material == null ? Blocks.STONE.getDefaultState() : material.getDefaultState();
        return MinecraftClient.getInstance().getBlockRenderManager().getModel(state).getParticleSprite();
    }

    /// Entry point for the loader-specific item renderers.
    public static void renderItem(ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        render(geometry(), sprite(CarvedGravestoneBlock.readMaterial(stack)), matrices, vertexConsumers, light, overlay);
    }

    public static void render(BakedModel model, Sprite target, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(target.getAtlasId()));
        MatrixStack.Entry entry = matrices.peek();
        Random random = Random.create();

        for (Direction direction : Direction.values()) {
            random.setSeed(42L);
            emitQuads(model.getQuads(null, direction, random), target, entry, consumer, light, overlay);
        }

        random.setSeed(42L);
        emitQuads(model.getQuads(null, null, random), target, entry, consumer, light, overlay);
    }

    private static void emitQuads(List<BakedQuad> quads, Sprite target, MatrixStack.Entry entry, VertexConsumer consumer, int light, int overlay) {
        for (BakedQuad quad : quads) {
            Sprite source = quad.getSprite();
            int[] vertexData = quad.getVertexData();
            Vec3i face = quad.getFace().getVector();

            for (int vertex = 0; vertex < 4; ++vertex) {
                int offset = vertex * VERTEX_STRIDE;
                consumer.vertex(entry.getPositionMatrix(),
                                Float.intBitsToFloat(vertexData[offset]),
                                Float.intBitsToFloat(vertexData[offset + 1]),
                                Float.intBitsToFloat(vertexData[offset + 2]))
                        .color(255, 255, 255, 255)
                        .texture(remap(Float.intBitsToFloat(vertexData[offset + 4]), source.getMinU(), source.getMaxU(), target.getMinU(), target.getMaxU()),
                                 remap(Float.intBitsToFloat(vertexData[offset + 5]), source.getMinV(), source.getMaxV(), target.getMinV(), target.getMaxV()))
                        .overlay(overlay)
                        .light(light)
                        .normal(entry.getNormalMatrix(), face.getX(), face.getY(), face.getZ())
                        .next();
            }
        }
    }

    private static float remap(float value, float sourceMin, float sourceMax, float targetMin, float targetMax) {
        float span = sourceMax - sourceMin;
        float fraction = span == 0.0F ? 0.0F : (value - sourceMin) / span;
        return targetMin + fraction * (targetMax - targetMin);
    }
}
