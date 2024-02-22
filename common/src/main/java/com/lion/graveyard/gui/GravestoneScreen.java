package com.lion.graveyard.gui;

import com.lion.graveyard.blockentities.GravestoneBlockEntity;
import com.lion.graveyard.blockentities.renders.GravestoneBlockEntityRenderer;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.joml.Matrix4f;

import java.util.Objects;
import java.util.stream.IntStream;

public class GravestoneScreen extends Screen {
    private final GravestoneBlockEntity sign;
    private int ticksSinceOpened;
    private int currentRow;
    private TextFieldHelper selectionManager;
    private WoodType signType;
    private SignRenderer.SignModel model;
    private String[] text;
    private SignText signText;

    public GravestoneScreen(GravestoneBlockEntity blockEntity, boolean filtered) {
        this(blockEntity, filtered, Component.translatable("gravestone.edit"));
    }

    public GravestoneScreen(GravestoneBlockEntity blockEntity, boolean filtered, Component title) {
        super(title);
        this.sign = blockEntity;
        this.signText = blockEntity.getText();
        this.signType = SignBlock.getWoodType(blockEntity.getBlockState().getBlock());
        this.text = IntStream.range(0, 4).mapToObj((line) -> this.signText.getMessage(line, filtered)).map(Component::getString).toArray(String[]::new);
    }


    protected void init() {
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (button) -> {
            this.finishEditing();
        }).bounds(this.width / 2 - 100, this.height / 4 + 144, 200, 20).build());
        this.selectionManager = new TextFieldHelper(
                () -> this.text[this.currentRow], this::setCurrentRowMessage, TextFieldHelper.createClipboardGetter(this.minecraft), TextFieldHelper.createClipboardSetter(this.minecraft),
                (string) -> this.minecraft.font.width(string) <= this.sign.getMaxTextWidth());

        this.signType = WoodType.OAK;
        this.model = GravestoneBlockEntityRenderer.createSignModel(this.minecraft.getEntityModels(), this.signType);
    }

    private void setCurrentRowMessage(String message) {
        this.text[this.currentRow] = message;
        this.signText = this.signText.setMessage(this.currentRow, Component.literal(message));
        this.sign.setText(this.signText);
    }

    public void removed() {
        ClientPacketListener clientPlayNetworkHandler = this.minecraft.getConnection();
        if (clientPlayNetworkHandler != null) {
            clientPlayNetworkHandler.send(new ServerboundSignUpdatePacket(this.sign.getBlockPos(), true, this.text[0], this.text[1], this.text[2], this.text[3]));
        }

    }

    public void tick() {
        ++this.ticksSinceOpened;
        if (!this.sign.getType().isValid(this.sign.getBlockState())) {
            this.finishEditing();
        }

    }

    private void finishEditing() {
        this.sign.setChanged();
        this.minecraft.setScreen(null);
    }

    public boolean charTyped(char chr, int modifiers) {
        this.selectionManager.charTyped(chr);
        return true;
    }

    public void close() {
        this.finishEditing();
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 265) {
            this.currentRow = this.currentRow - 1 & 3;
            this.selectionManager.setCursorToEnd();
            return true;
        } else if (keyCode != 264 && keyCode != 257 && keyCode != 335) {
            return this.selectionManager.keyPressed(keyCode) || super.keyPressed(keyCode, scanCode, modifiers);
        } else {
            this.currentRow = this.currentRow + 1 & 3;
            this.selectionManager.setCursorToEnd();
            return true;
        }
    }

    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        PoseStack matrices = context.pose();

        Lighting.setupForFlatItems();
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredString(this.font, this.title, this.width / 2, 40, 16777215);
        matrices.pushPose();
        matrices.translate((double) (this.width / 2), 0.0D, 50.0D);

        matrices.scale(93.75F, -93.75F, 93.75F);
        matrices.translate(0.0D, -1.3125D, 0.0D);
        BlockState blockState = this.sign.getBlockState();
        boolean bl = blockState.getBlock() instanceof SignBlock;
        if (!bl) {
            matrices.translate(0.0D, -0.3125D, 0.0D);
        }

        matrices.pushPose();
        matrices.scale(0.6666667F, -0.6666667F, -0.6666667F);
        MultiBufferSource.BufferSource immediate = context.bufferSource();

        SignRenderer.SignModel var10002 = this.model;
        Objects.requireNonNull(var10002);

        VertexConsumer vertexConsumer2 = GravestoneBlockEntityRenderer.getConsumer(immediate, sign.getBlockState().getBlock());

        this.model.stick.visible = false;
        this.model.root.render(matrices, vertexConsumer2, 15728880, OverlayTexture.NO_OVERLAY);
        matrices.popPose();

        matrices.translate(0.0D, 0.3333333432674408D, 0.046666666865348816D);
        matrices.scale(0.010416667F, -0.010416667F, 0.010416667F);
        int i = this.signText.hasGlowingText() ? this.signText.getColor().getTextColor() : SignRenderer.getDarkColor(this.signText);
        int j = this.selectionManager.getCursorPos();
        int k = this.selectionManager.getSelectionPos();
        int l = this.text.length * 5;

        int m = this.currentRow * 10 - l;
        int n;
        String string;
        int o;
        int p;
        for(n = 0; n < this.text.length; ++n) {
            string = this.text[n];
            if (string != null) {
                if (this.font.isBidirectional()) {
                    string = this.font.bidirectionalShaping(string);
                }

                int f = (-this.font.width(string) / 2);
                context.drawString(this.font, string, f, (n * 10 - l), i, false);
                if (n == this.currentRow && j >= 0 && bl) {
                    o = this.font.width(string.substring(0, Math.min(j, string.length())));
                    p = o - this.font.width(string) / 2;
                    if (j >= string.length()) {
                        context.drawString(this.font, "_", p, m, i, false);
                    }
                }
            }
        }

        immediate.endBatch();

        for(n = 0; n < this.text.length; ++n) {
            string = this.text[n];
            if (string != null && n == this.currentRow && j >= 0) {
                int q = this.font.width(string.substring(0, Math.min(j, string.length())));
                o = q - this.font.width(string) / 2;
                if (bl && j < string.length()) {
                    context.fill(o, m - 1, o + 1, m + 10, -16777216 | i);
                }

                if (k != j) {
                    p = Math.min(j, k);
                    int r = Math.max(j, k);
                    int s = this.minecraft.font.width(string.substring(0, p)) - this.minecraft.font.width(string) / 2;
                    int t = this.minecraft.font.width(string.substring(0, r)) - this.minecraft.font.width(string) / 2;
                    int u = Math.min(s, t);
                    int v = Math.max(s, t);
                    RenderSystem.enableColorLogicOp();
                    RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
                    context.fill(u, m, v, m + 10, -16776961);
                    RenderSystem.disableColorLogicOp();
                }
            }
        }

        matrices.popPose();
        Lighting.setupFor3DItems();
        super.render(context, mouseX, mouseY, delta);
    }
}
