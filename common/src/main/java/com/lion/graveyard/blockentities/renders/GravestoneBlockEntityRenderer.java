package com.lion.graveyard.blockentities.renders;

import com.google.common.collect.Maps;
import com.lion.graveyard.blockentities.GravestoneBlockEntity;
import com.lion.graveyard.blocks.GravestoneBlock;
import com.lion.graveyard.init.TGBlocks;
import com.lion.graveyard.util.GravestoneIdentifier;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.StandingSignRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PlainSignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3fc;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class GravestoneBlockEntityRenderer
        implements BlockEntityRenderer<GravestoneBlockEntity, GravestoneBlockEntityRenderer.State> {
    private static final int RENDER_DISTANCE = Mth.square(16);
    private final Font textRenderer;
    private final ItemModelResolver itemModelResolver;
    private static final HashMap<Block, RenderType> LAYERS = Maps.newHashMap();
    private static RenderType defaultLayer;

    public GravestoneBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.textRenderer = ctx.font();
        this.itemModelResolver = ctx.itemModelResolver();
    }

    public static class State extends BlockEntityRenderState {
        public FormattedCharSequence[] lines = new FormattedCharSequence[0];
        public int textColor;
        public int outlineColor;
        public int textLight;
        public Direction facing = Direction.NORTH;
        public final ItemStackRenderState stone = new ItemStackRenderState();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(
            GravestoneBlockEntity gravestone,
            State state,
            float partialTick,
            Vec3 cameraPosition,
            ModelFeatureRenderer.CrumblingOverlay crumblingOverlay
    ) {
        BlockEntityRenderer.super.extractRenderState(gravestone, state, partialTick, cameraPosition, crumblingOverlay);
        int darkColor = getColor(gravestone);

        state.lines = gravestone.getText().getRenderMessages(Minecraft.getInstance().isTextFilteringEnabled(), (text) -> {
            List<FormattedCharSequence> list = this.textRenderer.split(text, gravestone.getMaxTextWidth());
            return list.isEmpty() ? FormattedCharSequence.EMPTY : list.get(0);
        });

        if (gravestone.getText().hasGlowingText()) {
            state.textColor = gravestone.getText().getColor().getTextColor();
            state.outlineColor = shouldRender(gravestone, state.textColor) ? darkColor : 0;
            state.textLight = 15728880;
        } else {
            state.textColor = darkColor;
            state.outlineColor = 0;
            state.textLight = state.lightCoords;
        }

        BlockState blockState = gravestone.getBlockState();
        state.facing = blockState.getValue(GravestoneBlock.FACING);
        state.stone.clear();
        ItemStack stack = new ItemStack(blockState.getBlock().asItem(), 1);
        this.itemModelResolver.updateForTopItem(state.stone, stack, ItemDisplayContext.GROUND, gravestone.getLevel(), null, 2);
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        poseStack.pushPose();

        poseStack.translate(0.5D, 0.25D, 0.5D);

        float rotation = -state.facing.toYRot();
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        poseStack.translate(0.0D, 0.3333333432674408D, 0.23D);

        poseStack.scale(0.010416667F, -0.010416667F, 0.010416667F);

        for (int line = 0; line < 4; ++line) {
            FormattedCharSequence text = state.lines[line];
            float x = (float) (-this.textRenderer.width(text) / 2);

            collector.submitText(
                    poseStack,
                    x,
                    (float) (line * 10 - 20),
                    text,
                    false,
                    Font.DisplayMode.NORMAL,
                    state.textLight,
                    state.textColor,
                    0,
                    state.outlineColor
            );
        }

        poseStack.popPose();

        submitGrave(state, poseStack, collector);
    }

    private void submitGrave(State state, PoseStack poseStack, SubmitNodeCollector collector) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.43, 0.5);
        poseStack.scale(2.28F, 2.15F, 2.28F);
        float rotation = state.facing.toYRot();
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));

        state.stone.submit(
                poseStack,
                CrumblingItemCollector.wrap(collector, state.breakProgress),
                state.lightCoords,
                OverlayTexture.NO_OVERLAY,
                0
        );

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    private static boolean shouldRender(GravestoneBlockEntity sign, int signColor) {
        if (signColor == DyeColor.BLACK.getTextColor()) {
            return true;
        } else {
            Minecraft minecraftClient = Minecraft.getInstance();
            LocalPlayer clientPlayerEntity = minecraftClient.player;

            if (clientPlayerEntity != null && minecraftClient.options.getCameraType().isFirstPerson() && clientPlayerEntity.isScoping()) {
                return true;
            } else {
                Entity entity = minecraftClient.getCameraEntity();
                return entity != null && entity.distanceToSqr(Vec3.atCenterOf(sign.getBlockPos())) < (double)RENDER_DISTANCE;
            }
        }
    }

    static int getColor(GravestoneBlockEntity sign) {
        int i = sign.getText().getColor().getTextColor();

        if (i == DyeColor.BLACK.getTextColor() && sign.getText().hasGlowingText()) {
            return -988212;
        } else {
            double d = 0.4D;
            int j = (int)((double) ARGB.red(i) * d);
            int k = (int)((double) ARGB.green(i) * d);
            int l = (int)((double) ARGB.blue(i) * d);
            return ARGB.color(255, j, k, l);
        }
    }

    public static Model.Simple createSignModel(EntityModelSet entityModelLoader, WoodType type) {
        return StandingSignRenderer.createSignModel(entityModelLoader, type, PlainSignBlock.Attachment.WALL);
    }

    static {
        defaultLayer = RenderTypes.entitySolid(Identifier.withDefaultNamespace("textures/entity/signs/oak.png"));
        LAYERS.put(TGBlocks.GRAVESTONE.get(), RenderTypes.entitySolid(GravestoneIdentifier.POLISHED_BASALT_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.COBBLESTONE_GRAVESTONE.get(), RenderTypes.entitySolid(GravestoneIdentifier.COBBLESTONE_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.MOSSY_COBBLESTONE_GRAVESTONE.get(), RenderTypes.entitySolid(GravestoneIdentifier.MOSSY_COBBLESTONE_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.DEEPSLATE_GRAVESTONE.get(), RenderTypes.entitySolid(GravestoneIdentifier.DEEPSLATE_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.BLACKSTONE_GRAVESTONE.get(), RenderTypes.entitySolid(GravestoneIdentifier.BLACKSTONE_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.CRACKED_BLACKSTONE_GRAVESTONE.get(), RenderTypes.entitySolid(GravestoneIdentifier.CRACKED_BLACKSTONE_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.STONE_BRICKS_GRAVESTONE.get(), RenderTypes.entitySolid(GravestoneIdentifier.STONE_BRICKS_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.MOSSY_STONE_BRICKS_GRAVESTONE.get(), RenderTypes.entitySolid(GravestoneIdentifier.MOSSY_STONE_BRICKS_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.BRICKS_GRAVESTONE.get(), RenderTypes.entitySolid(GravestoneIdentifier.BRICKS_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.RED_SANDSTONE_GRAVESTONE.get(), RenderTypes.entitySolid(GravestoneIdentifier.RED_SANDSTONE_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.SANDSTONE_GRAVESTONE.get(), RenderTypes.entitySolid(GravestoneIdentifier.SANDSTONE_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.GILDED_BLACKSTONE_GRAVESTONE.get(), RenderTypes.entitySolid(GravestoneIdentifier.GILDED_BLACKSTONE_GRAVESTONE_TEXTURE));
        LAYERS.put(TGBlocks.QUARTZ_BRICKS_GRAVESTONE.get(), RenderTypes.entitySolid(GravestoneIdentifier.QUARTZ_BRICKS_GRAVESTONE_TEXTURE));
    }

    public static VertexConsumer getConsumer(MultiBufferSource provider, Block block) {
        return provider.getBuffer(LAYERS.getOrDefault(block, defaultLayer));
    }

    /// Sends item quads through submitModel, which draws the crumbling overlay.
    public static final class CrumblingItemCollector implements SubmitNodeCollector {
        private final SubmitNodeCollector delegate;
        private final ModelFeatureRenderer.CrumblingOverlay crumblingOverlay;

        private CrumblingItemCollector(
                SubmitNodeCollector delegate,
                ModelFeatureRenderer.CrumblingOverlay crumblingOverlay
        ) {
            this.delegate = delegate;
            this.crumblingOverlay = crumblingOverlay;
        }

        public static SubmitNodeCollector wrap(
                SubmitNodeCollector collector,
                ModelFeatureRenderer.CrumblingOverlay crumblingOverlay
        ) {
            return crumblingOverlay == null ? collector : new CrumblingItemCollector(collector, crumblingOverlay);
        }

        @Override
        public void submitItem(
                PoseStack poseStack,
                ItemDisplayContext displayContext,
                int light,
                int overlay,
                int outlineColor,
                int[] tintLayers,
                List<BakedQuad> quads,
                ItemStackRenderState.FoilType foilType
        ) {
            Map<RenderType, List<BakedQuad>> quadsByRenderType = new LinkedHashMap<>();

            for (BakedQuad quad : quads) {
                quadsByRenderType
                        .computeIfAbsent(quad.materialInfo().itemRenderType(), renderType -> new ArrayList<>())
                        .add(quad);
            }

            quadsByRenderType.forEach((renderType, group) -> this.delegate.submitModel(
                    new QuadModel(group, renderType),
                    group,
                    poseStack,
                    renderType,
                    light,
                    overlay,
                    -1,
                    null,
                    outlineColor,
                    this.crumblingOverlay
            ));
        }

        @Override
        public OrderedSubmitNodeCollector order(int order) {
            return this.delegate.order(order);
        }

        @Override
        public void submitShadow(PoseStack poseStack, float radius, List<EntityRenderState.ShadowPiece> pieces) {
            this.delegate.submitShadow(poseStack, radius, pieces);
        }

        @Override
        public void submitNameTag(
                PoseStack poseStack,
                Vec3 position,
                int offset,
                Component name,
                boolean seeThrough,
                int light,
                double distanceSq,
                CameraRenderState cameraState
        ) {
            this.delegate.submitNameTag(poseStack, position, offset, name, seeThrough, light, distanceSq, cameraState);
        }

        @Override
        public void submitText(
                PoseStack poseStack,
                float x,
                float y,
                FormattedCharSequence text,
                boolean dropShadow,
                Font.DisplayMode displayMode,
                int light,
                int color,
                int backgroundColor,
                int outlineColor
        ) {
            this.delegate.submitText(
                    poseStack,
                    x,
                    y,
                    text,
                    dropShadow,
                    displayMode,
                    light,
                    color,
                    backgroundColor,
                    outlineColor
            );
        }

        @Override
        public void submitFlame(PoseStack poseStack, EntityRenderState state, Quaternionf rotation) {
            this.delegate.submitFlame(poseStack, state, rotation);
        }

        @Override
        public void submitLeash(PoseStack poseStack, EntityRenderState.LeashState leashState) {
            this.delegate.submitLeash(poseStack, leashState);
        }

        @Override
        public <S> void submitModel(
                Model<? super S> model,
                S state,
                PoseStack poseStack,
                RenderType renderType,
                int light,
                int overlay,
                int color,
                TextureAtlasSprite sprite,
                int outlineColor,
                ModelFeatureRenderer.CrumblingOverlay crumbling
        ) {
            this.delegate.submitModel(
                    model,
                    state,
                    poseStack,
                    renderType,
                    light,
                    overlay,
                    color,
                    sprite,
                    outlineColor,
                    crumbling
            );
        }

        @Override
        public void submitModelPart(
                ModelPart part,
                PoseStack poseStack,
                RenderType renderType,
                int light,
                int overlay,
                TextureAtlasSprite sprite,
                boolean sheeted,
                boolean hasFoil,
                int color,
                ModelFeatureRenderer.CrumblingOverlay crumbling,
                int outlineColor
        ) {
            this.delegate.submitModelPart(
                    part,
                    poseStack,
                    renderType,
                    light,
                    overlay,
                    sprite,
                    sheeted,
                    hasFoil,
                    color,
                    crumbling,
                    outlineColor
            );
        }

        @Override
        public void submitMovingBlock(PoseStack poseStack, MovingBlockRenderState movingBlock) {
            this.delegate.submitMovingBlock(poseStack, movingBlock);
        }

        @Override
        public void submitBlockModel(
                PoseStack poseStack,
                RenderType renderType,
                List<BlockStateModelPart> parts,
                int[] tintLayers,
                int light,
                int overlay,
                int outlineColor
        ) {
            this.delegate.submitBlockModel(poseStack, renderType, parts, tintLayers, light, overlay, outlineColor);
        }

        @Override
        public void submitBreakingBlockModel(PoseStack poseStack, BlockStateModel model, long seed, int progress) {
            this.delegate.submitBreakingBlockModel(poseStack, model, seed, progress);
        }

        @Override
        public void submitCustomGeometry(
                PoseStack poseStack,
                RenderType renderType,
                SubmitNodeCollector.CustomGeometryRenderer renderer
        ) {
            this.delegate.submitCustomGeometry(poseStack, renderType, renderer);
        }

        @Override
        public void submitParticleGroup(SubmitNodeCollector.ParticleGroupRenderer renderer) {
            this.delegate.submitParticleGroup(renderer);
        }
    }

    /// Model that holds baked item quads as polygons, in pixel units, with atlas UVs.
    private static final class QuadModel extends Model<List<BakedQuad>> {
        private QuadModel(List<BakedQuad> quads, RenderType renderType) {
            super(createRoot(quads), id -> renderType);
        }

        private static ModelPart createRoot(List<BakedQuad> quads) {
            List<ModelPart.Cube> cubes = new ArrayList<>();

            for (BakedQuad quad : quads) {
                ModelPart.Vertex[] vertices = new ModelPart.Vertex[BakedQuad.VERTEX_COUNT];

                for (int i = 0; i < vertices.length; i++) {
                    Vector3fc position = quad.position(i);
                    long uv = quad.packedUV(i);
                    vertices[i] = new ModelPart.Vertex(
                            position.x() * 16.0F,
                            position.y() * 16.0F,
                            position.z() * 16.0F,
                            UVPair.unpackU(uv),
                            UVPair.unpackV(uv)
                    );
                }

                ModelPart.Cube cube = new ModelPart.Cube(
                        0,
                        0,
                        0.0F,
                        0.0F,
                        0.0F,
                        0.0F,
                        0.0F,
                        0.0F,
                        0.0F,
                        0.0F,
                        0.0F,
                        false,
                        1.0F,
                        1.0F,
                        EnumSet.of(Direction.UP)
                );
                cube.polygons[0] = new ModelPart.Polygon(vertices, quad.direction().getUnitVec3f());
                cubes.add(cube);
            }

            return new ModelPart(cubes, Map.of());
        }
    }
}
