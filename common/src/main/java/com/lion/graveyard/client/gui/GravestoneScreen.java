package main.java.com.lion.graveyard.client.gui;

import com.finallion.graveyard.blockentities.GravestoneBlockEntity;
import com.finallion.graveyard.blockentities.renders.GravestoneBlockEntityRenderer;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SignBlock;
import net.minecraft.block.WoodType;
import net.minecraft.block.entity.SignText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.util.SelectionManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.joml.Matrix4f;

import java.util.Objects;
import java.util.stream.IntStream;

@Environment(EnvType.CLIENT)
public class GravestoneScreen extends Screen {
    private final GravestoneBlockEntity sign;
    private int ticksSinceOpened;
    private int currentRow;
    private SelectionManager selectionManager;
    private WoodType signType;
    private SignBlockEntityRenderer.SignModel model;
    private String[] text;
    private SignText signText;

    public GravestoneScreen(GravestoneBlockEntity blockEntity, boolean filtered) {
        this(blockEntity, filtered, Text.translatable("gravestone.edit"));
    }

    public GravestoneScreen(GravestoneBlockEntity blockEntity, boolean filtered, Text title) {
        super(title);
        this.sign = blockEntity;
        this.signText = blockEntity.getText();
        this.signType = AbstractSignBlock.getWoodType(blockEntity.getCachedState().getBlock());
        this.text = IntStream.range(0, 4).mapToObj((line) -> this.signText.getMessage(line, filtered)).map(Text::getString).toArray(String[]::new);
    }


    protected void init() {
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, (button) -> {
            this.finishEditing();
        }).dimensions(this.width / 2 - 100, this.height / 4 + 144, 200, 20).build());
        this.selectionManager = new SelectionManager(
                () -> this.text[this.currentRow], this::setCurrentRowMessage, SelectionManager.makeClipboardGetter(this.client), SelectionManager.makeClipboardSetter(this.client),
                (string) -> this.client.textRenderer.getWidth(string) <= this.sign.getMaxTextWidth());

        this.signType = WoodType.OAK;
        this.model = GravestoneBlockEntityRenderer.createSignModel(this.client.getEntityModelLoader(), this.signType);
    }

    private void setCurrentRowMessage(String message) {
        this.text[this.currentRow] = message;
        this.signText = this.signText.withMessage(this.currentRow, Text.literal(message));
        this.sign.setText(this.signText);
    }

    public void removed() {
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.getNetworkHandler();
        if (clientPlayNetworkHandler != null) {
            clientPlayNetworkHandler.sendPacket(new UpdateSignC2SPacket(this.sign.getPos(), true, this.text[0], this.text[1], this.text[2], this.text[3]));
        }

    }

    public void tick() {
        ++this.ticksSinceOpened;
        if (!this.sign.getType().supports(this.sign.getCachedState())) {
            this.finishEditing();
        }

    }

    private void finishEditing() {
        this.sign.markDirty();
        this.client.setScreen(null);
    }

    public boolean charTyped(char chr, int modifiers) {
        this.selectionManager.insert(chr);
        return true;
    }

    public void close() {
        this.finishEditing();
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 265) {
            this.currentRow = this.currentRow - 1 & 3;
            this.selectionManager.putCursorAtEnd();
            return true;
        } else if (keyCode != 264 && keyCode != 257 && keyCode != 335) {
            return this.selectionManager.handleSpecialKey(keyCode) || super.keyPressed(keyCode, scanCode, modifiers);
        } else {
            this.currentRow = this.currentRow + 1 & 3;
            this.selectionManager.putCursorAtEnd();
            return true;
        }
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        MatrixStack matrices = context.getMatrices();

        DiffuseLighting.disableGuiDepthLighting();
        this.renderBackground(context);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 40, 16777215);
        matrices.push();
        matrices.translate((double) (this.width / 2), 0.0D, 50.0D);

        matrices.scale(93.75F, -93.75F, 93.75F);
        matrices.translate(0.0D, -1.3125D, 0.0D);
        BlockState blockState = this.sign.getCachedState();
        boolean bl = blockState.getBlock() instanceof SignBlock;
        if (!bl) {
            matrices.translate(0.0D, -0.3125D, 0.0D);
        }

        matrices.push();
        matrices.scale(0.6666667F, -0.6666667F, -0.6666667F);
        VertexConsumerProvider.Immediate immediate = this.client.getBufferBuilders().getEntityVertexConsumers();

        SignBlockEntityRenderer.SignModel var10002 = this.model;
        Objects.requireNonNull(var10002);

        VertexConsumer vertexConsumer2 = GravestoneBlockEntityRenderer.getConsumer(immediate, sign.getCachedState().getBlock());

        this.model.stick.visible = false;
        this.model.root.render(matrices, vertexConsumer2, 15728880, OverlayTexture.DEFAULT_UV);
        matrices.pop();

        matrices.translate(0.0D, 0.3333333432674408D, 0.046666666865348816D);
        matrices.scale(0.010416667F, -0.010416667F, 0.010416667F);
        int i = this.signText.getColor().getSignColor();
        int j = this.selectionManager.getSelectionStart();
        int k = this.selectionManager.getSelectionEnd();
        int l = this.text.length * 5;
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();

        int m = this.currentRow * 10 - l;
        int n;
        String string;
        int o;
        int p;
        for(n = 0; n < this.text.length; ++n) {
            string = this.text[n];
            if (string != null) {
                if (this.textRenderer.isRightToLeft()) {
                    string = this.textRenderer.mirror(string);
                }

                float f = (float)(-this.client.textRenderer.getWidth(string) / 2);
                this.client.textRenderer.draw(string, f, (float)(n * 10 - l), i, false, matrix4f, immediate, TextRenderer.TextLayerType.NORMAL, 0, 15728880, false);
                if (n == this.currentRow && j >= 0 && bl) {
                    o = this.client.textRenderer.getWidth(string.substring(0, Math.min(j, string.length())));
                    p = o - this.client.textRenderer.getWidth(string) / 2;
                    if (j >= string.length()) {
                        this.client.textRenderer.draw("_", (float)p, (float)m, i, false, matrix4f, immediate, TextRenderer.TextLayerType.NORMAL, 0, 15728880, false);
                    }
                }
            }
        }

        immediate.draw();

        for(n = 0; n < this.text.length; ++n) {
            string = this.text[n];
            if (string != null && n == this.currentRow && j >= 0) {
                int q = this.client.textRenderer.getWidth(string.substring(0, Math.min(j, string.length())));
                o = q - this.client.textRenderer.getWidth(string) / 2;
                if (bl && j < string.length()) {
                    context.fill(o, m - 1, o + 1, m + 10, -16777216 | i);
                }

                if (k != j) {
                    p = Math.min(j, k);
                    int r = Math.max(j, k);
                    int s = this.client.textRenderer.getWidth(string.substring(0, p)) - this.client.textRenderer.getWidth(string) / 2;
                    int t = this.client.textRenderer.getWidth(string.substring(0, r)) - this.client.textRenderer.getWidth(string) / 2;
                    int u = Math.min(s, t);
                    int v = Math.max(s, t);
                    RenderSystem.enableColorLogicOp();
                    RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
                    context.fill(u, m, v, m + 10, -16776961);
                    RenderSystem.disableColorLogicOp();
                }
            }
        }

        matrices.pop();
        DiffuseLighting.enableGuiDepthLighting();
        super.render(context, mouseX, mouseY, delta);
    }
}
