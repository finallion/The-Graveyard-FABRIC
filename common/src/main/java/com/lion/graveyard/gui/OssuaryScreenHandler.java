package com.lion.graveyard.gui;

import com.google.common.collect.Lists;
import com.lion.graveyard.init.TGBlocks;
import com.lion.graveyard.init.TGScreens;
import com.lion.graveyard.recipe.OssuaryRecipe;
import com.lion.graveyard.init.TGRecipeTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import java.util.List;

public class OssuaryScreenHandler extends AbstractContainerMenu {
    private final ContainerLevelAccess context;
    private final DataSlot selectedRecipe;
    private final Level world;
    private List<RecipeHolder<OssuaryRecipe>> availableRecipes;
    private ItemStack inputStack;
    long lastTakeTime;
    final Slot inputSlot;
    final Slot outputSlot;
    Runnable contentsChangedListener;
    public final Container input;
    final ResultContainer output;

    public OssuaryScreenHandler(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, ContainerLevelAccess.NULL);
    }

    public OssuaryScreenHandler(int syncId, Inventory playerInventory, ContainerLevelAccess context) {
        super(TGScreens.OSSUARY_SCREEN_HANDLER, syncId);
        this.selectedRecipe = DataSlot.standalone();
        this.availableRecipes = Lists.newArrayList();
        this.inputStack = ItemStack.EMPTY;
        this.contentsChangedListener = () -> {
        };
        this.input = new SimpleContainer(1) {
            public void setChanged() {
                super.setChanged();
                OssuaryScreenHandler.this.onContentChanged(this);
                OssuaryScreenHandler.this.contentsChangedListener.run();
            }
        };
        this.output = new ResultContainer();
        this.context = context;
        this.world = playerInventory.player.level();
        this.inputSlot = this.addSlot(new Slot(this.input, 0, 20, 33));
        this.outputSlot = this.addSlot(new Slot(this.output, 1, 143, 33) {
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            public void onTake(Player player, ItemStack stack) {
                stack.onCraftedBy(player.level(), player, stack.getCount());
                OssuaryScreenHandler.this.output.awardUsedRecipes(player, this.getInputStacks());
                ItemStack itemStack = OssuaryScreenHandler.this.inputSlot.remove(1);
                if (!itemStack.isEmpty()) {
                    OssuaryScreenHandler.this.populateResult();
                }

                context.execute((world, pos) -> {
                    long l = world.getGameTime();
                    if (OssuaryScreenHandler.this.lastTakeTime != l) {
                        world.playSound((Player)null, pos, SoundEvents.CHAIN_BREAK, SoundSource.BLOCKS, 1.0F, -3.0F);
                        OssuaryScreenHandler.this.lastTakeTime = l;
                    }

                });
                super.onTake(player, stack);
            }

            private List<ItemStack> getInputStacks() {
                return List.of(OssuaryScreenHandler.this.inputSlot.getItem());
            }
        });

        int i;
        for(i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }

        this.addDataSlot(this.selectedRecipe);
    }

    public int getSelectedRecipe() {
        return this.selectedRecipe.get();
    }

    public List<RecipeHolder<OssuaryRecipe>> getAvailableRecipes() {
        return this.availableRecipes;
    }

    public int getAvailableRecipeCount() {
        return this.availableRecipes.size();
    }

    public boolean canCraft() {
        return this.inputSlot.hasItem() && !this.availableRecipes.isEmpty();
    }

    public boolean stillValid(Player player) {
        return stillValid(this.context, player, TGBlocks.OSSUARY.get());
    }

    public boolean clickMenuButton(Player player, int id) {
        if (this.isInBounds(id)) {
            this.selectedRecipe.set(id);
            this.populateResult();
        }

        return true;
    }

    private boolean isInBounds(int id) {
        return id >= 0 && id < this.availableRecipes.size();
    }

    public void onContentChanged(Container inventory) {
        ItemStack itemStack = this.inputSlot.getItem();
        if (!itemStack.is(this.inputStack.getItem())) {
            this.inputStack = itemStack.copy();
            this.updateInput(inventory, itemStack);
        }

    }

    private void updateInput(Container input, ItemStack stack) {
        this.availableRecipes.clear();
        this.selectedRecipe.set(-1);
        this.outputSlot.set(ItemStack.EMPTY);
        if (!stack.isEmpty()) {
            this.availableRecipes = this.world.getRecipeManager().getRecipesFor(TGRecipeTypes.OSSUARY_CARVING.get(), input, this.world);
        }

    }

    void populateResult() {
        if (!this.availableRecipes.isEmpty() && this.isInBounds(this.selectedRecipe.get())) {
            RecipeHolder<OssuaryRecipe> carvingRecipe = this.availableRecipes.get(this.selectedRecipe.get());
            ItemStack itemStack = carvingRecipe.value().assemble(this.input, this.world.registryAccess());
            if (itemStack.isItemEnabled(this.world.enabledFeatures())) {
                this.output.setRecipeUsed(carvingRecipe);
                this.outputSlot.set(itemStack);
            } else {
                this.outputSlot.set(ItemStack.EMPTY);
            }
        } else {
            this.outputSlot.set(ItemStack.EMPTY);
        }

        this.broadcastChanges();
    }

    public MenuType<?> getType() {
        return TGScreens.OSSUARY_SCREEN_HANDLER;
    }

    public void setContentsChangedListener(Runnable contentsChangedListener) {
        this.contentsChangedListener = contentsChangedListener;
    }

    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return slot.container != this.output && super.canTakeItemForPickAll(stack, slot);
    }

    public ItemStack quickMoveStack(Player player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = (Slot)this.slots.get(slot);
        if (slot2 != null && slot2.hasItem()) {
            ItemStack itemStack2 = slot2.getItem();
            Item item = itemStack2.getItem();
            itemStack = itemStack2.copy();
            if (slot == 1) {
                item.onCraftedBy(itemStack2, player.level(), player);
                if (!this.moveItemStackTo(itemStack2, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }

                slot2.onQuickCraft(itemStack2, itemStack);
            } else if (slot == 0) {
                if (!this.moveItemStackTo(itemStack2, 2, 38, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.world.getRecipeManager().getRecipeFor(TGRecipeTypes.OSSUARY_CARVING.get(), new SimpleContainer(itemStack2), this.world).isPresent()) {
                if (!this.moveItemStackTo(itemStack2, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slot >= 2 && slot < 29) {
                if (!this.moveItemStackTo(itemStack2, 29, 38, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slot >= 29 && slot < 38 && !this.moveItemStackTo(itemStack2, 2, 29, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot2.set(ItemStack.EMPTY);
            }

            slot2.setChanged();
            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot2.onTake(player, itemStack2);
            this.broadcastChanges();
        }

        return itemStack;
    }

    public void removed(Player player) {
        super.removed(player);
        this.output.removeItemNoUpdate(1);
        this.context.execute((world, pos) -> {
            this.clearContainer(player, this.input);
        });
    }
}
