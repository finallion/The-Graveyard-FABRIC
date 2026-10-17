package com.lion.graveyard.chronicle.client;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.mixin.client.HandledScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class ChronicleButton extends TexturedButtonWidget {

    private final HandledScreenAccessor inventory;

    private ChronicleButton(InventoryScreen inventory) {
        super(0, 0, 20, 20, 0, 0, 20, ChronicleScreen.BUTTON_TEXTURE, 20, 40,
                button -> MinecraftClient.getInstance().setScreen(new ChronicleScreen(inventory)));
        this.inventory = (HandledScreenAccessor) inventory;
        this.setTooltip(Tooltip.of(Text.translatable("chronicle.graveyard.open")));
        this.followInventory();
    }

    public static void addTo(Screen screen, Consumer<ClickableWidget> addWidget) {
        if (screen instanceof InventoryScreen inventory && Graveyard.getConfig().flag("chronicleInventoryButton", true)) {
            addWidget.accept(new ChronicleButton(inventory));
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
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
        MinecraftClient client = MinecraftClient.getInstance();
        return client.player != null && !client.player.getStatusEffects().isEmpty();
    }
}
