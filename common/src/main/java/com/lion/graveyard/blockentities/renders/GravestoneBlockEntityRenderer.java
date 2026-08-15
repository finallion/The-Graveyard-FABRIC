package com.lion.graveyard.blockentities.renders;

import com.google.common.collect.Maps;
import com.lion.graveyard.blockentities.GravestoneBlockEntity;
import com.lion.graveyard.blocks.CarvedGravestoneBlock;
import com.lion.graveyard.blocks.GravestoneBlock;
import com.lion.graveyard.init.TGBlocks;
import com.lion.graveyard.util.GravestoneIdentifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;

@Environment(EnvType.CLIENT)
public class GravestoneBlockEntityRenderer implements BlockEntityRenderer<GravestoneBlockEntity> {
    private static final int RENDER_DISTANCE = MathHelper.square(16);
    /// Ints per vertex in a baked quad: position (3), colour, uv (2), light, normal.
    private static final int VERTEX_STRIDE = 8;
    private final TextRenderer textRenderer;
    private static final HashMap<Block, RenderLayer> LAYERS = Maps.newHashMap();
    private static RenderLayer defaultLayer;

    public GravestoneBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.textRenderer = ctx.getTextRenderer();
    }

    public void render(GravestoneBlockEntity signBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        BlockState blockState = signBlockEntity.getCachedState();
        World world = signBlockEntity.getWorld();
        matrixStack.push();

        // text render location in world
        // offset on block
        matrixStack.translate(0.5D, 0.25D, 0.5D);

        float rotation = -blockState.get(GravestoneBlock.FACING).asRotation();
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
        matrixStack.push();

        // size of block
        matrixStack.scale(0.6666667F, -0.6666667F, -0.6666667F);
        matrixStack.pop();
        CarvedGravestoneBlock carved = blockState.getBlock() instanceof CarvedGravestoneBlock block ? block : null;
        matrixStack.translate(0.0D,
                carved == null ? 0.3333333432674408D : carved.textHeight(),
                carved == null ? 0.23D : carved.textDepth());
        matrixStack.scale(0.010416667F, -0.010416667F, 0.010416667F);

        int m = getColor(signBlockEntity);

        OrderedText[] orderedTexts = signBlockEntity.getText().getOrderedMessages(MinecraftClient.getInstance().shouldFilterText(), (text) -> {
            List<OrderedText> list = this.textRenderer.wrapLines(text, signBlockEntity.getMaxTextWidth());
            return list.isEmpty() ? OrderedText.EMPTY : (OrderedText)list.get(0);
        });

        int q;
        boolean bl2;
        int r;
        if (signBlockEntity.getText().isGlowing()) {
            q = signBlockEntity.getText().getColor().getSignColor();
            bl2 = shouldRender(signBlockEntity, q);
            r = 15728880;
        } else {
            q = m;
            bl2 = false;
            r = i;
        }

        for(int s = 0; s < 4; ++s) {
            OrderedText orderedText = orderedTexts[s];
            float t = (float)(-this.textRenderer.getWidth(orderedText) / 2);
            if (bl2) {
                this.textRenderer.drawWithOutline(orderedText, t, (float)(s * 10 - 20), q, m, matrixStack.peek().getPositionMatrix(), vertexConsumerProvider, r);
            } else {
                this.textRenderer.draw(orderedText, t, (float)(s * 10 - 20), q, false, matrixStack.peek().getPositionMatrix(), vertexConsumerProvider, TextRenderer.TextLayerType.NORMAL, 0, r);
            }

        }

        matrixStack.pop();

        if (carved != null && !carved.drawsFromItemModel()) {
            renderBlockModelGrave(signBlockEntity, blockState, matrixStack, vertexConsumerProvider, i, j);
        } else {
            renderGrave(signBlockEntity, blockState, f, matrixStack, vertexConsumerProvider, i, j, world);
        }
    }

    public void renderGrave(GravestoneBlockEntity gravestone, BlockState state, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, World world) {
        matrixStack.push();
        matrixStack.translate(0.5, 0.43, 0.5);
        matrixStack.scale(2.28F, 2.15F, 2.28F);
        float rotation = state.get(GravestoneBlock.FACING).asRotation();
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));

        Block material = gravestone.getMaterial();
        if (material == null) {
            ItemStack stack = new ItemStack(state.getBlock().asItem(), 1);
            MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformationMode.GROUND, i, j, matrixStack, vertexConsumerProvider, world, 2);
        } else {
            renderWithMaterial(material, matrixStack, vertexConsumerProvider, i, j);
        }

        matrixStack.pop();
    }

    private static void renderWithMaterial(Block material, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, int overlay) {
        BakedModel model = CarvedGravestoneRenderer.geometry();

        matrixStack.push();
        // Mirrors what the item renderer does before it draws a model, so a carved gravestone sits
        // exactly where the pre-carving ones sit.
        model.getTransformation().getTransformation(ModelTransformationMode.GROUND).apply(false, matrixStack);
        matrixStack.translate(-0.5F, -0.5F, -0.5F);

        CarvedGravestoneRenderer.render(model, CarvedGravestoneRenderer.sprite(material), matrixStack, vertexConsumerProvider, light, overlay);
        matrixStack.pop();
    }

    /// Drawn straight in block coordinates: the model is authored at the size it should have and
    /// needs none of the item transformation the small gravestone inherited.
    private void renderBlockModelGrave(GravestoneBlockEntity gravestone, BlockState state, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, int overlay) {
        matrixStack.push();
        matrixStack.translate(0.5D, 0.0D, 0.5D);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-state.get(GravestoneBlock.FACING).asRotation()));
        matrixStack.translate(-0.5D, 0.0D, -0.5D);

        BakedModel model = CarvedGravestoneRenderer.geometry(state.getBlock());
        CarvedGravestoneRenderer.render(model, CarvedGravestoneRenderer.sprite(gravestone.getMaterial()), matrixStack, vertexConsumerProvider, light, overlay);
        matrixStack.pop();
    }

    /// The tall gravestone reaches into the block above, which would otherwise cull it as soon as the
    /// lower half leaves the view.
    @Override
    public boolean rendersOutsideBoundingBox(GravestoneBlockEntity blockEntity) {
        return true;
    }

    private static boolean shouldRender(GravestoneBlockEntity sign, int signColor) {
        if (signColor == DyeColor.BLACK.getSignColor()) {
            return true;
        } else {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            ClientPlayerEntity clientPlayerEntity = minecraftClient.player;
            if (clientPlayerEntity != null && minecraftClient.options.getPerspective().isFirstPerson() && clientPlayerEntity.isUsingSpyglass()) {
                return true;
            } else {
                Entity entity = minecraftClient.getCameraEntity();
                return entity != null && entity.squaredDistanceTo(Vec3d.ofCenter(sign.getPos())) < (double)RENDER_DISTANCE;
            }
        }
    }

    static int getColor(GravestoneBlockEntity sign) {
        int i = sign.getText().getColor().getSignColor();
        if (i == DyeColor.BLACK.getSignColor() && sign.getText().isGlowing()) {
            return -988212;
        } else {
            double d = 0.4D;
            int j = (int)((double) ColorHelper.Argb.getRed(i) * d);
            int k = (int)((double) ColorHelper.Argb.getGreen(i) * d);
            int l = (int)((double) ColorHelper.Argb.getBlue(i) * d);
            return ColorHelper.Argb.getArgb(0, j, k, l);
        }
    }

    public static SignBlockEntityRenderer.SignModel createSignModel(EntityModelLoader entityModelLoader, WoodType type) {
        return new SignBlockEntityRenderer.SignModel(entityModelLoader.getModelPart(EntityModelLayers.createSign(type)));
    }

    static {
        defaultLayer = RenderLayer.getEntitySolid(new Identifier("textures/entity/signs/oak.png"));
        LAYERS.put(TGBlocks.GRAVESTONE.get(), RenderLayer.getEntitySolid(GravestoneIdentifier.POLISHED_BASALT_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.COBBLESTONE_GRAVESTONE.get(), RenderLayer.getEntitySolid(GravestoneIdentifier.COBBLESTONE_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.MOSSY_COBBLESTONE_GRAVESTONE.get(), RenderLayer.getEntitySolid(GravestoneIdentifier.MOSSY_COBBLESTONE_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.DEEPSLATE_GRAVESTONE.get(), RenderLayer.getEntitySolid(GravestoneIdentifier.DEEPSLATE_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.BLACKSTONE_GRAVESTONE.get(), RenderLayer.getEntitySolid(GravestoneIdentifier.BLACKSTONE_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.CRACKED_BLACKSTONE_GRAVESTONE.get(), RenderLayer.getEntitySolid(GravestoneIdentifier.CRACKED_BLACKSTONE_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.STONE_BRICKS_GRAVESTONE.get(), RenderLayer.getEntitySolid(GravestoneIdentifier.STONE_BRICKS_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.MOSSY_STONE_BRICKS_GRAVESTONE.get(), RenderLayer.getEntitySolid(GravestoneIdentifier.MOSSY_STONE_BRICKS_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.BRICKS_GRAVESTONE.get(), RenderLayer.getEntitySolid(GravestoneIdentifier.BRICKS_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.RED_SANDSTONE_GRAVESTONE.get(), RenderLayer.getEntitySolid(GravestoneIdentifier.RED_SANDSTONE_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.SANDSTONE_GRAVESTONE.get(), RenderLayer.getEntitySolid(GravestoneIdentifier.SANDSTONE_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.GILDED_BLACKSTONE_GRAVESTONE.get(), RenderLayer.getEntitySolid(GravestoneIdentifier.GILDED_BLACKSTONE_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.QUARTZ_BRICKS_GRAVESTONE.get(), RenderLayer.getEntitySolid(GravestoneIdentifier.QUARTZ_BRICKS_GRAVESTONE_TEXTURE));
    }

    public static VertexConsumer getConsumer(VertexConsumerProvider provider, Block block) {
        return provider.getBuffer(LAYERS.getOrDefault(block, defaultLayer));
    }

    /// The board in the edit screen. A carved gravestone has no drawn texture of its own, so its
    /// material's sprite is wrapped the way vanilla wraps sign wood types: the model keeps its own
    /// 0..1 coordinates and they are folded into the sprite's window on the atlas.
    public static VertexConsumer getConsumer(VertexConsumerProvider provider, GravestoneBlockEntity gravestone) {
        Block material = gravestone.getMaterial();
        if (material == null) {
            return getConsumer(provider, gravestone.getCachedState().getBlock());
        }

        Sprite sprite = CarvedGravestoneRenderer.sprite(material);
        return sprite.getTextureSpecificVertexConsumer(provider.getBuffer(RenderLayer.getEntityCutout(sprite.getAtlasId())));
    }
}

