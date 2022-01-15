package com.finallion.graveyard.client.gui;

import com.finallion.graveyard.blockentities.GravestoneBlockEntity;
import com.finallion.graveyard.blockentities.renders.GravestoneBlockEntityRenderer;
import com.finallion.graveyard.blocks.GravestoneBlock;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.SignBlock;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.util.SelectionManager;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.SignType;
import net.minecraft.util.Util;
import net.minecraft.util.math.Matrix4f;

import java.util.Arrays;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class GravestoneScreen extends Screen {
    private final GravestoneBlockEntity sign;
    private int ticksSinceOpened;
    private int currentRow;
    private SelectionManager selectionManager;
    private SignType signType;
    private SignBlockEntityRenderer.SignModel model;
    private final String[] text = (String[]) Util.make(new String[4], (strings) -> {
        Arrays.fill(strings, "");
    });

    public GravestoneScreen(GravestoneBlockEntity sign, boolean filtered) {
        super(new TranslatableText("gravestone.edit"));
        this.sign = sign;
    }

    protected void init() {
        this.client.keyboard.setRepeatEvents(true);
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120, 200, 20, ScreenTexts.DONE, (button) -> {
            this.finishEditing();
        }));
        this.sign.setEditable(false);
        this.selectionManager = new SelectionManager(() -> {
            return this.text[this.currentRow];
        }, (text) -> {
            this.text[this.currentRow] = text;
            this.sign.setTextOnRow(this.currentRow, new LiteralText(text));
        }, SelectionManager.makeClipboardGetter(this.client), SelectionManager.makeClipboardSetter(this.client), (text) -> {
            return this.client.textRenderer.getWidth(text) <= 90;
        });
        BlockState blockState = this.sign.getCachedState();
        this.signType = GravestoneBlockEntityRenderer.getSignType(blockState.getBlock());
        this.model = GravestoneBlockEntityRenderer.createSignModel(this.client.getEntityModelLoader(), this.signType);
    }

    public void removed() {
        this.client.keyboard.setRepeatEvents(false);
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.getNetworkHandler();
        if (clientPlayNetworkHandler != null) {
            clientPlayNetworkHandler.sendPacket(new UpdateSignC2SPacket(this.sign.getPos(), this.text[0], this.text[1], this.text[2], this.text[3]));
        }

        this.sign.setEditable(true);
    }

    public void tick() {
        ++this.ticksSinceOpened;
        if (!this.sign.getType().supports(this.sign.getCachedState())) {
            this.finishEditing();
        }

    }

    private void finishEditing() {
        this.sign.markDirty();
        this.client.setScreen((Screen)null);
    }

    public boolean charTyped(char chr, int modifiers) {
        this.selectionManager.insert(chr);
        return true;
    }

    public void onClose() {
        this.finishEditing();
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 265) {
            this.currentRow = this.currentRow - 1 & 3;
            this.selectionManager.putCursorAtEnd();
            return true;
        } else if (keyCode != 264 && keyCode != 257 && keyCode != 335) {
            return this.selectionManager.handleSpecialKey(keyCode) ? true : super.keyPressed(keyCode, scanCode, modifiers);
        } else {
            this.currentRow = this.currentRow + 1 & 3;
            this.selectionManager.putCursorAtEnd();
            return true;
        }
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        DiffuseLighting.disableGuiDepthLighting();
        this.renderBackground(matrices);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 40, 16777215);
        matrices.push();
        matrices.translate((double) (this.width / 2), 0.0D, 50.0D);
        float f = 93.75F;
        matrices.scale(93.75F, -93.75F, 93.75F);
        matrices.translate(0.0D, -1.3125D, 0.0D);
        BlockState blockState = this.sign.getCachedState();
        boolean bl = blockState.getBlock() instanceof SignBlock;
        if (!bl) {
            matrices.translate(0.0D, -0.3125D, 0.0D);
        }

        boolean bl2 = this.ticksSinceOpened / 6 % 2 == 0;
        float g = 0.6666667F;
        matrices.push();
        matrices.scale(0.6666667F, -0.6666667F, -0.6666667F);
        VertexConsumerProvider.Immediate immediate = this.client.getBufferBuilders().getEntityVertexConsumers();
        SpriteIdentifier spriteIdentifier = new SpriteIdentifier(TexturedRenderLayers.SIGNS_ATLAS_TEXTURE, ((GravestoneBlock) sign.getCachedState().getBlock()).getTexture());
        //SpriteIdentifier spriteIdentifier = TexturedRenderLayers.getSignTextureId(SignType.WARPED);
        SignBlockEntityRenderer.SignModel var10002 = this.model;
        Objects.requireNonNull(var10002);
        VertexConsumer vertexConsumer = spriteIdentifier.getVertexConsumer(immediate, var10002::getLayer);
        VertexConsumer vertexConsumer2 = GravestoneBlockEntityRenderer.getConsumer(immediate, sign.getCachedState().getBlock());

        this.model.stick.visible = false;
        this.model.root.render(matrices, vertexConsumer2, 15728880, OverlayTexture.DEFAULT_UV);
        matrices.pop();
        float h = 0.010416667F;
        matrices.translate(0.0D, 0.3333333432674408D, 0.046666666865348816D);
        matrices.scale(0.010416667F, -0.010416667F, 0.010416667F);
        int i = this.sign.getTextColor().getSignColor();
        int j = this.selectionManager.getSelectionStart();
        int k = this.selectionManager.getSelectionEnd();
        int l = this.currentRow * 10 - this.text.length * 5;
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();

        int m;
        String string2;
        int s;
        int t;
        for (m = 0; m < this.text.length; ++m) {
            string2 = this.text[m];
            if (string2 != null) {
                if (this.textRenderer.isRightToLeft()) {
                    string2 = this.textRenderer.mirror(string2);
                }

                float n = (float) (-this.client.textRenderer.getWidth(string2) / 2);
                this.client.textRenderer.draw(string2, n, (float) (m * 10 - this.text.length * 5), i, false, matrix4f, immediate, false, 0, 15728880, false);
                if (m == this.currentRow && j >= 0 && bl2) {
                    s = this.client.textRenderer.getWidth(string2.substring(0, Math.max(Math.min(j, string2.length()), 0)));
                    t = s - this.client.textRenderer.getWidth(string2) / 2;
                    if (j >= string2.length()) {
                        this.client.textRenderer.draw("_", (float) t, (float) l, i, false, matrix4f, immediate, false, 0, 15728880, false);
                    }
                }
            }
        }

        immediate.draw();

        for (m = 0; m < this.text.length; ++m) {
            string2 = this.text[m];
            if (string2 != null && m == this.currentRow && j >= 0) {
                int r = this.client.textRenderer.getWidth(string2.substring(0, Math.max(Math.min(j, string2.length()), 0)));
                s = r - this.client.textRenderer.getWidth(string2) / 2;
                if (bl2 && j < string2.length()) {
                    int var31 = l - 1;
                    int var10003 = s + 1;
                    Objects.requireNonNull(this.client.textRenderer);
                    fill(matrices, s, var31, var10003, l + 9, -16777216 | i);
                }

                if (k != j) {
                    t = Math.min(j, k);
                    int u = Math.max(j, k);
                    int v = this.client.textRenderer.getWidth(string2.substring(0, t)) - this.client.textRenderer.getWidth(string2) / 2;
                    int w = this.client.textRenderer.getWidth(string2.substring(0, u)) - this.client.textRenderer.getWidth(string2) / 2;
                    int x = Math.min(v, w);
                    int y = Math.max(v, w);
                    Tessellator tessellator = Tessellator.getInstance();
                    BufferBuilder bufferBuilder = tessellator.getBuffer();
                    RenderSystem.setShader(GameRenderer::getPositionColorShader);
                    RenderSystem.disableTexture();
                    RenderSystem.enableColorLogicOp();
                    RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
                    bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
                    float var32 = (float) x;
                    Objects.requireNonNull(this.client.textRenderer);
                    bufferBuilder.vertex(matrix4f, var32, (float) (l + 9), 0.0F).color(0, 0, 255, 255).next();
                    var32 = (float) y;
                    Objects.requireNonNull(this.client.textRenderer);
                    bufferBuilder.vertex(matrix4f, var32, (float) (l + 9), 0.0F).color(0, 0, 255, 255).next();
                    bufferBuilder.vertex(matrix4f, (float) y, (float) l, 0.0F).color(0, 0, 255, 255).next();
                    bufferBuilder.vertex(matrix4f, (float) x, (float) l, 0.0F).color(0, 0, 255, 255).next();
                    bufferBuilder.end();
                    BufferRenderer.draw(bufferBuilder);
                    RenderSystem.disableColorLogicOp();
                    RenderSystem.enableTexture();
                }
            }
        }

        matrices.pop();
        DiffuseLighting.enableGuiDepthLighting();
        super.render(matrices, mouseX, mouseY, delta);
    }
}
