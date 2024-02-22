package com.lion.graveyard.gui;

import com.lion.graveyard.recipe.OssuaryRecipe;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

@Environment(EnvType.CLIENT)
public class OssuaryScreen extends AbstractContainerScreen<OssuaryScreenHandler> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/stonecutter.png");
    private static final ResourceLocation SCROLLER_TEXTURE = new ResourceLocation("container/stonecutter/scroller");
    private static final ResourceLocation SCROLLER_DISABLED_TEXTURE = new ResourceLocation("container/stonecutter/scroller_disabled");
    private static final ResourceLocation RECIPE_SELECTED_SPRITE = new ResourceLocation("container/stonecutter/recipe_selected");
    private static final ResourceLocation RECIPE_HIGHLIGHTED_SPRITE = new ResourceLocation("container/stonecutter/recipe_highlighted");
    private static final ResourceLocation RECIPE_SPRITE = new ResourceLocation("container/stonecutter/recipe");
    private float scrollAmount;
    private boolean mouseClicked;
    private int scrollOffset;
    private boolean canCraft;

    public OssuaryScreen(OssuaryScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
        handler.setContentsChangedListener(this::onInventoryChange);
        --this.titleLabelY;
    }

    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.renderTooltip(context, mouseX, mouseY);
    }

    protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
        int i = this.leftPos;
        int j = this.topPos;
        context.blit(TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight);
        int k = (int)(41.0F * this.scrollAmount);
        ResourceLocation identifier = this.shouldScroll() ? SCROLLER_TEXTURE : SCROLLER_DISABLED_TEXTURE;
        context.blitSprite(identifier, i + 119, j + 15 + k, 12, 15);
        int l = this.leftPos + 52;
        int m = this.topPos + 14;
        int n = this.scrollOffset + 12;
        this.renderButtons(context, mouseX, mouseY, l, m, n);
        this.renderRecipeIcons(context, l, m, n);
    }

    protected void renderTooltip(GuiGraphics context, int x, int y) {
        super.renderTooltip(context, x, y);
        if (this.canCraft) {
            int i = this.leftPos + 52;
            int j = this.topPos + 14;
            int k = this.scrollOffset + 12;
            List<RecipeHolder<OssuaryRecipe>> list = this.menu.getAvailableRecipes();

            for(int l = this.scrollOffset; l < k && l < this.menu.getAvailableRecipeCount(); ++l) {
                int m = l - this.scrollOffset;
                int n = i + m % 4 * 16;
                int o = j + m / 4 * 18 + 2;
                if (x >= n && x < n + 16 && y >= o && y < o + 18) {
                    context.renderTooltip(this.font, list.get(l).value().getResultItem(this.minecraft.level.registryAccess()), x, y);
                }
            }
        }

    }
    private void renderButtons(GuiGraphics context, int mouseX, int mouseY, int x, int y, int scrollOffset) {
        for(int i = this.scrollOffset; i < scrollOffset && i < ((OssuaryScreenHandler)this.menu).getAvailableRecipeCount(); ++i) {
            int j = i - this.scrollOffset;
            int k = x + j % 4 * 16;
            int l = j / 4;
            int m = y + l * 18 + 2;
            ResourceLocation resourceLocation;
            if (i == ((OssuaryScreenHandler)this.menu).getSelectedRecipe()) {
                resourceLocation = RECIPE_SELECTED_SPRITE;
            } else if (mouseX >= k && mouseY >= m && mouseX < k + 16 && mouseY < m + 18) {
                resourceLocation = RECIPE_HIGHLIGHTED_SPRITE;
            } else {
                resourceLocation = RECIPE_SPRITE;
            }

            context.blitSprite(resourceLocation, k, m - 1, 16, 18);
        }

    }

    private void renderRecipeIcons(GuiGraphics context, int x, int y, int scrollOffset) {
        List<RecipeHolder<OssuaryRecipe>> list = ((OssuaryScreenHandler)this.menu).getAvailableRecipes();

        for(int i = this.scrollOffset; i < scrollOffset && i < ((OssuaryScreenHandler)this.menu).getAvailableRecipeCount(); ++i) {
            int j = i - this.scrollOffset;
            int k = x + j % 4 * 16;
            int l = j / 4;
            int m = y + l * 18 + 2;
            context.renderItem(((RecipeHolder<OssuaryRecipe>)list.get(i)).value().getResultItem(this.minecraft.level.registryAccess()), k, m);
        }

    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
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

        return super.mouseClicked(mouseX, mouseY, button);
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.mouseClicked && this.shouldScroll()) {
            int i = this.topPos + 14;
            int j = i + 54;
            this.scrollAmount = ((float)mouseY - (float)i - 7.5F) / ((float)(j - i) - 15.0F);
            this.scrollAmount = Mth.clamp(this.scrollAmount, 0.0F, 1.0F);
            this.scrollOffset = (int)((double)(this.scrollAmount * (float)this.getMaxScroll()) + 0.5D) * 4;
            return true;
        } else {
            return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (this.shouldScroll()) {
            int i = this.getMaxScroll();
            float f = (float)verticalAmount / (float)i;
            this.scrollAmount = Mth.clamp(this.scrollAmount - f, 0.0f, 1.0f);
            this.scrollOffset = (int)((double)(this.scrollAmount * (float)i) + 0.5) * 4;
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

