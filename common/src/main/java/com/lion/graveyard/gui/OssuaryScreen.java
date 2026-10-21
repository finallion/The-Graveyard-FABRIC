package com.lion.graveyard.gui;

import com.lion.graveyard.platform.OssuaryRecipesSync;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import java.util.List;

@Environment(EnvType.CLIENT)
public class OssuaryScreen extends AbstractContainerScreen<OssuaryScreenHandler> {
    private static final Identifier TEXTURE = Identifier.withDefaultNamespace("textures/gui/container/stonecutter.png");
    private static final Identifier SCROLLER_SPRITE = Identifier.withDefaultNamespace("container/stonecutter/scroller");
    private static final Identifier SCROLLER_DISABLED_SPRITE = Identifier.withDefaultNamespace("container/stonecutter/scroller_disabled");
    private static final Identifier RECIPE_SPRITE = Identifier.withDefaultNamespace("container/stonecutter/recipe");
    private static final Identifier RECIPE_SELECTED_SPRITE = Identifier.withDefaultNamespace("container/stonecutter/recipe_selected");
    private static final Identifier RECIPE_HIGHLIGHTED_SPRITE = Identifier.withDefaultNamespace("container/stonecutter/recipe_highlighted");
    private static final ItemStack INPUT_HINT = new ItemStack(Items.BONE_BLOCK);
    private static final Component INPUT_HINT_TEXT = Component.translatable("container.graveyard.ossuary.input_hint");
    private float scrollAmount;
    private boolean mouseClicked;
    private int scrollOffset;
    private boolean canCraft;

    public OssuaryScreen(OssuaryScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
        handler.setContentsChangedListener(this::onInventoryChange);
        --this.titleLabelY;
    }

    public static void receive(OssuaryRecipesSync payload) {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.containerMenu instanceof OssuaryScreenHandler handler && handler.containerId == payload.containerId()) {
            handler.setAvailableResults(payload.results());
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractBackground(context, mouseX, mouseY, delta);
        int i = this.leftPos;
        int j = this.topPos;
        context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
        int k = (int)(41.0F * this.scrollAmount);
        context.blitSprite(RenderPipelines.GUI_TEXTURED, this.shouldScroll() ? SCROLLER_SPRITE : SCROLLER_DISABLED_SPRITE, i + 119, j + 15 + k, 12, 15);
        int l = this.leftPos + 52;
        int m = this.topPos + 14;
        int n = this.scrollOffset + 12;
        this.extractRecipeBackground(context, mouseX, mouseY, l, m, n);
        this.extractRecipeIcons(context, l, m, n);
        Slot input = this.menu.getSlot(0);

        if (!input.hasItem()) {
            int sx = this.leftPos + input.x;
            int sy = this.topPos + input.y;
            context.fakeItem(INPUT_HINT, sx, sy);
            context.fill(sx, sy, sx + 16, sy + 16, 0x808B8B8B);
        }
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor context, int x, int y) {
        super.extractTooltip(context, x, y);
        Slot input = this.menu.getSlot(0);

        if (!input.hasItem() && this.menu.getCarried().isEmpty() && this.isHovering(input.x, input.y, 16, 16, x, y)) {
            context.setTooltipForNextFrame(this.font, INPUT_HINT_TEXT, x, y);
        }

        if (this.canCraft) {
            int i = this.leftPos + 52;
            int j = this.topPos + 14;
            int k = this.scrollOffset + 12;
            List<ItemStack> list = ((OssuaryScreenHandler)this.menu).getAvailableResults();

            for(int l = this.scrollOffset; l < k && l < ((OssuaryScreenHandler)this.menu).getAvailableRecipeCount(); ++l) {
                int m = l - this.scrollOffset;
                int n = i + m % 4 * 16;
                int o = j + m / 4 * 18 + 2;

                if (x >= n && x < n + 16 && y >= o && y < o + 18) {
                    context.setTooltipForNextFrame(this.font, list.get(l), x, y);
                }
            }
        }

    }
    private void extractRecipeBackground(
            GuiGraphicsExtractor context,
            int mouseX,
            int mouseY,
            int x,
            int y,
            int scrollOffset
    ) {
        for(int i = this.scrollOffset; i < scrollOffset && i < ((OssuaryScreenHandler)this.menu).getAvailableRecipeCount(); ++i) {
            int j = i - this.scrollOffset;
            int k = x + j % 4 * 16;
            int l = j / 4;
            int m = y + l * 18 + 2;
            Identifier sprite = RECIPE_SPRITE;

            if (i == ((OssuaryScreenHandler)this.menu).getSelectedRecipe()) {
                sprite = RECIPE_SELECTED_SPRITE;
            } else if (mouseX >= k && mouseY >= m && mouseX < k + 16 && mouseY < m + 18) {
                sprite = RECIPE_HIGHLIGHTED_SPRITE;
            }

            context.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, k, m - 1, 16, 18);
        }

    }

    private void extractRecipeIcons(GuiGraphicsExtractor context, int x, int y, int scrollOffset) {
        List<ItemStack> list = ((OssuaryScreenHandler)this.menu).getAvailableResults();

        for(int i = this.scrollOffset; i < scrollOffset && i < ((OssuaryScreenHandler)this.menu).getAvailableRecipeCount(); ++i) {
            int j = i - this.scrollOffset;
            int k = x + j % 4 * 16;
            int l = j / 4;
            int m = y + l * 18 + 2;
            context.item(list.get(i), k, m);
        }

    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        this.mouseClicked = false;

        if (this.canCraft) {
            int i = this.leftPos + 52;
            int j = this.topPos + 14;
            int k = this.scrollOffset + 12;

            for(int l = this.scrollOffset; l < k; ++l) {
                int m = l - this.scrollOffset;
                double d = mouseX - (double)(i + m % 4 * 16);
                double e = mouseY - (double)(j + m / 4 * 18);

                if (d >= 0.0D && e >= 0.0D && d < 16.0D && e < 18.0D && ((OssuaryScreenHandler)this.menu).clickMenuButton(this.minecraft.player, l)) {
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, -1.0F));
                    this.minecraft.gameMode.handleInventoryButtonClick(((OssuaryScreenHandler)this.menu).containerId, l);
                    return true;
                }
            }

            i = this.leftPos + 119;
            j = this.topPos + 9;

            if (mouseX >= (double)i && mouseX < (double)(i + 12) && mouseY >= (double)j && mouseY < (double)(j + 54)) {
                this.mouseClicked = true;
            }
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        if (this.mouseClicked && this.shouldScroll()) {
            int i = this.topPos + 14;
            int j = i + 54;
            this.scrollAmount = ((float)event.y() - (float)i - 7.5F) / ((float)(j - i) - 15.0F);
            this.scrollAmount = Mth.clamp(this.scrollAmount, 0.0F, 1.0F);
            this.scrollOffset = (int)((double)(this.scrollAmount * (float)this.getMaxScroll()) + 0.5D) * 4;
            return true;
        } else {
            return super.mouseDragged(event, deltaX, deltaY);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double amount) {
        if (this.shouldScroll()) {
            int i = this.getMaxScroll();
            float f = (float)amount / (float)i;
            this.scrollAmount = Mth.clamp(this.scrollAmount - f, 0.0F, 1.0F);
            this.scrollOffset = (int)((double)(this.scrollAmount * (float)i) + 0.5D) * 4;
        }

        return true;
    }

    private boolean shouldScroll() {
        return this.canCraft && ((OssuaryScreenHandler)this.menu).getAvailableRecipeCount() > 12;
    }

    protected int getMaxScroll() {
        return (((OssuaryScreenHandler)this.menu).getAvailableRecipeCount() + 4 - 1) / 4 - 3;
    }

    private void onInventoryChange() {
        this.canCraft = ((OssuaryScreenHandler)this.menu).canCraft();

        if (!this.canCraft) {
            this.scrollAmount = 0.0F;
            this.scrollOffset = 0;
        }

    }
}

