package com.lion.graveyard.chronicle.client;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.init.TGItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/// The small "page found" notice, drawn on the vanilla advancement-toast frame.
@Environment(EnvType.CLIENT)
public class ChronicleToast implements Toast {
    private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/gui/toasts.png");
    private static final long DURATION = 5000L;

    private final Component title;
    private final Component description;
    private final ItemStack icon;

    public ChronicleToast(String page) {
        this.title = Component.translatable("chronicle." + Graveyard.MOD_ID + ".toast");
        this.description = Component.translatable("chronicle." + Graveyard.MOD_ID + ".page." + page + ".title");
        this.icon = new ItemStack(TGItems.DIARY_PAGE.get());
    }

    @Override
    public Visibility render(GuiGraphics context, ToastComponent manager, long startTime) {
        context.blit(TEXTURE, 0, 0, 0, 0, this.width(), this.height());
        context.drawString(manager.getMinecraft().font, this.title, 30, 7, 0xFF500050, false);
        context.drawString(manager.getMinecraft().font, this.description, 30, 18, 0xFF000000, false);
        context.renderItem(this.icon, 8, 8);
        return startTime >= DURATION ? Visibility.HIDE : Visibility.SHOW;
    }
}
