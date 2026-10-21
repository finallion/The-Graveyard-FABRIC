package com.lion.graveyard.chronicle.client;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.init.TGItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

/// The small "page found" notice, drawn on the vanilla advancement-toast frame.
@Environment(EnvType.CLIENT)
public class ChronicleToast implements Toast {
    private static final Identifier BACKGROUND = Identifier.withDefaultNamespace("toast/advancement");
    private static final long DURATION = 5000L;

    private final Component title;
    private final Component description;
    private final ItemStack icon;
    private Visibility wantedVisibility = Visibility.SHOW;

    public ChronicleToast(String page) {
        this.title = Component.translatable("chronicle." + Graveyard.MOD_ID + ".toast");
        this.description = Component.translatable("chronicle." + Graveyard.MOD_ID + ".page." + page + ".title");
        this.icon = new ItemStack(TGItems.DIARY_PAGE.get());
    }

    @Override
    public Visibility getWantedVisibility() {
        return this.wantedVisibility;
    }

    @Override
    public void update(ToastManager manager, long visibleTime) {
        this.wantedVisibility = visibleTime >= DURATION ? Visibility.HIDE : Visibility.SHOW;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, Font font, long visibleTime) {
        context.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, 0, 0, this.width(), this.height());
        context.text(font, this.title, 30, 7, 0xFF500050, false);
        context.text(font, this.description, 30, 18, 0xFF000000, false);
        context.item(this.icon, 8, 8);
    }
}
