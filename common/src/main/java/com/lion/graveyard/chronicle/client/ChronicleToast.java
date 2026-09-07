package com.lion.graveyard.chronicle.client;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.init.TGItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/// The small "page found" notice, drawn on the vanilla advancement-toast frame.
@Environment(EnvType.CLIENT)
public class ChronicleToast implements Toast {
    private static final Identifier TEXTURE = new Identifier("textures/gui/toasts.png");
    private static final long DURATION = 5000L;

    private final Text title;
    private final Text description;
    private final ItemStack icon;

    public ChronicleToast(String page) {
        this.title = Text.translatable("chronicle." + Graveyard.MOD_ID + ".toast");
        this.description = Text.translatable("chronicle." + Graveyard.MOD_ID + ".page." + page + ".title");
        this.icon = new ItemStack(TGItems.DIARY_PAGE.get());
    }

    @Override
    public Visibility draw(DrawContext context, ToastManager manager, long startTime) {
        context.drawTexture(TEXTURE, 0, 0, 0, 0, this.getWidth(), this.getHeight());
        context.drawText(manager.getClient().textRenderer, this.title, 30, 7, 0xFF500050, false);
        context.drawText(manager.getClient().textRenderer, this.description, 30, 18, 0xFF000000, false);
        context.drawItem(this.icon, 8, 8);
        return startTime >= DURATION ? Visibility.HIDE : Visibility.SHOW;
    }
}
