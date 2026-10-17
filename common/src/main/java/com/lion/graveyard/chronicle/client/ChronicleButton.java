package com.lion.graveyard.chronicle.client;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.mixin.client.HandledScreenAccessor;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;

public class ChronicleButton extends ImageButton {

    private final HandledScreenAccessor inventory;

    private ChronicleButton(InventoryScreen inventory) {
        super(0, 0, 20, 20, 0, 0, 20, ChronicleScreen.BUTTON_TEXTURE, 20, 40,
                button -> Minecraft.getInstance().setScreen(new ChronicleScreen(inventory)));
        this.inventory = (HandledScreenAccessor) inventory;
        this.setTooltip(Tooltip.create(Component.translatable("chronicle.graveyard.open")));
        this.followInventory();
    }

    public static void addTo(Screen screen, Consumer<AbstractWidget> addWidget) {
        if (screen instanceof InventoryScreen inventory && Graveyard.getConfig().flag("chronicleInventoryButton", true)) {
            addWidget.accept(new ChronicleButton(inventory));
        }
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        this.followInventory();
        super.render(context, mouseX, mouseY, delta);
    }

    private void followInventory() {
        int right = this.inventory.graveyard$getX() + this.inventory.graveyard$getBackgroundWidth();
        int top = this.inventory.graveyard$getY();
        // Vanilla draws the status effects to the right of the inventory, over this button.
        if (hasStatusEffects()) {
            this.setPosition(right - this.getWidth(), top - this.getHeight() - 2);
        } else {
            this.setPosition(right + 2, top + 4);
        }
    }

    private static boolean hasStatusEffects() {
        Minecraft client = Minecraft.getInstance();
        return client.player != null && !client.player.getActiveEffects().isEmpty();
    }
}
