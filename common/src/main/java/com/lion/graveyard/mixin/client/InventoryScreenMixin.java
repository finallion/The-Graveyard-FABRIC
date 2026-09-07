package com.lion.graveyard.mixin.client;

import com.lion.graveyard.chronicle.client.ChronicleScreen;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/// The chronicle has no item; a small book button beside the inventory opens it.
@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> {

    private InventoryScreenMixin() {
        super(null, null, null);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void graveyard$addChronicleButton(CallbackInfo ci) {
        TexturedButtonWidget button = new TexturedButtonWidget(this.x + this.backgroundWidth + 2, this.y + 4, 20, 20, 0, 0, 20,
                ChronicleScreen.BUTTON_TEXTURE, 20, 40, b -> this.client.setScreen(new ChronicleScreen(this)));
        button.setTooltip(net.minecraft.client.gui.tooltip.Tooltip.of(Text.translatable("chronicle.graveyard.open")));
        this.addDrawableChild(button);
    }
}
