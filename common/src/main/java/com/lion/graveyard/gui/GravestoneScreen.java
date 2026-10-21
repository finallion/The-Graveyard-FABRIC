package com.lion.graveyard.gui;

import com.lion.graveyard.blockentities.GravestoneBlockEntity;
import com.lion.graveyard.blockentities.renders.GravestoneBlockEntityRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.model.Model;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.stream.IntStream;

@Environment(EnvType.CLIENT)
public class GravestoneScreen extends Screen {
    private static final float SIGN_SCALE = 62.500004F;
    private static final float TEXT_SCALE = 0.9765628F;
    private static final float SIGN_Y_OFFSET = 90.0F;
    private final GravestoneBlockEntity sign;
    private int ticksSinceOpened;
    private int currentRow;
    private TextFieldHelper selectionManager;
    private WoodType signType;
    private Model.Simple model;
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

    @Override
    protected void init() {
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (button) -> {
            this.finishEditing();
        }).bounds(this.width / 2 - 100, this.height / 4 + 144, 200, 20).build());
        this.selectionManager = new TextFieldHelper(
                () -> this.text[this.currentRow],
                this::setCurrentRowMessage,
                TextFieldHelper.createClipboardGetter(this.minecraft),
                TextFieldHelper.createClipboardSetter(this.minecraft),
                (string) -> this.minecraft.font.width(string) <= this.sign.getMaxTextWidth()
        );

        this.signType = WoodType.OAK;
        this.model = GravestoneBlockEntityRenderer.createSignModel(this.minecraft.getEntityModels(), this.signType);
    }

    private void setCurrentRowMessage(String message) {
        this.text[this.currentRow] = message;
        this.signText = this.signText.setMessage(this.currentRow, Component.literal(message));
        this.sign.setText(this.signText);
    }

    @Override
    public void removed() {
        ClientPacketListener clientPlayNetworkHandler = this.minecraft.getConnection();

        if (clientPlayNetworkHandler != null) {
            clientPlayNetworkHandler.send(new ServerboundSignUpdatePacket(this.sign.getBlockPos(), true, this.text[0], this.text[1], this.text[2], this.text[3]));
        }

    }

    @Override
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

    @Override
    public boolean charTyped(CharacterEvent event) {
        this.selectionManager.charTyped(event);
        return true;
    }

    @Override
    public void onClose() {
        this.finishEditing();
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        int keyCode = event.key();

        if (keyCode == 265) {
            this.currentRow = this.currentRow - 1 & 3;
            this.selectionManager.setCursorToEnd();
            return true;
        } else if (keyCode != 264 && keyCode != 257 && keyCode != 335) {
            return this.selectionManager.keyPressed(event) || super.keyPressed(event);
        } else {
            this.currentRow = this.currentRow + 1 & 3;
            this.selectionManager.setCursorToEnd();
            return true;
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        this.extractTransparentBackground(context);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractRenderState(context, mouseX, mouseY, delta);
        context.centeredText(this.font, this.title, this.width / 2, 40, 0xFFFFFFFF);
        context.pose().pushMatrix();
        context.pose().translate((float) this.width / 2.0F, SIGN_Y_OFFSET);
        context.pose().pushMatrix();
        this.extractSignBackground(context);
        context.pose().popMatrix();
        this.extractSignText(context);
        context.pose().popMatrix();
    }

    private void extractSignBackground(GuiGraphicsExtractor context) {
        if (this.model == null) {
            return;
        }

        int center = this.width / 2;
        ScreenRectangle scissor = context.scissorStack.peek();
        context.guiRenderState.addPicturesInPictureState(
                new GravestoneSignRenderState(this.model, this.sign.getBlockState().getBlock(), center - 48, 66, center + 48, 168, SIGN_SCALE, scissor)
        );
    }

    private void extractSignText(GuiGraphicsExtractor context) {
        context.pose().scale(TEXT_SCALE, TEXT_SCALE);
        int color = ARGB.opaque(this.signText.getColor().getTextColor());
        boolean caretVisible = this.ticksSinceOpened / 6 % 2 == 0;
        int cursor = this.selectionManager.getCursorPos();
        int selection = this.selectionManager.getSelectionPos();
        int lineHeight = this.sign.getTextLineHeight();
        int halfHeight = 4 * lineHeight / 2;
        int caretY = this.currentRow * lineHeight - halfHeight;

        for (int row = 0; row < this.text.length; ++row) {
            String line = this.text[row];

            if (line == null) {
                continue;
            }

            if (this.font.isBidirectional()) {
                line = this.font.bidirectionalShaping(line);
            }

            int x = -this.font.width(line) / 2;
            context.text(this.font, line, x, row * lineHeight - halfHeight, color, false);

            if (row == this.currentRow && cursor >= 0 && caretVisible && cursor >= line.length()) {
                int caretX = this.font.width(line.substring(0, Math.min(cursor, line.length()))) - this.font.width(line) / 2;
                context.text(this.font, "_", caretX, caretY, color, false);
            }
        }

        String current = this.text[this.currentRow];

        if (current == null || cursor < 0) {
            return;
        }

        int halfWidth = this.font.width(current) / 2;

        if (caretVisible && cursor < current.length()) {
            int caretX = this.font.width(current.substring(0, cursor)) - halfWidth;
            context.fill(caretX, caretY - 1, caretX + 1, caretY + lineHeight, color);
        }

        if (selection != cursor) {
            int fromX = this.font.width(current.substring(0, Math.min(cursor, selection))) - halfWidth;
            int toX = this.font.width(current.substring(0, Math.max(cursor, selection))) - halfWidth;
            context.textHighlight(Math.min(fromX, toX), caretY, Math.max(fromX, toX), caretY + lineHeight, true);
        }
    }
}
