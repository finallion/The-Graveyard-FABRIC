package com.lion.graveyard.gui;

import com.google.common.collect.Lists;
import com.lion.graveyard.init.TGBlocks;
import com.lion.graveyard.init.TGScreens;
import com.lion.graveyard.recipe.OssuaryRecipe;
import com.lion.graveyard.platform.OssuaryRecipesSync;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import java.util.Comparator;
import java.util.List;

public class OssuaryScreenHandler extends AbstractContainerMenu {
    private final ContainerLevelAccess context;
    private final DataSlot selectedRecipe;
    private final Level world;
    private final Player player;
    private List<RecipeHolder<OssuaryRecipe>> availableRecipes;
    private List<ItemStack> availableResults;
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
        this.availableResults = Lists.newArrayList();
        this.player = playerInventory.player;
        this.inputStack = ItemStack.EMPTY;
        this.contentsChangedListener = () -> {
        };
        this.input = new SimpleContainer(1) {
            @Override
            public void setChanged() {
                super.setChanged();
                OssuaryScreenHandler.this.slotsChanged(this);
                OssuaryScreenHandler.this.contentsChangedListener.run();
            }
        };
        this.output = new ResultContainer();
        this.context = context;
        this.world = playerInventory.player.level();
        this.inputSlot = this.addSlot(new Slot(this.input, 0, 20, 33));
        this.outputSlot = this.addSlot(new Slot(this.output, 1, 143, 33) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                stack.onCraftedBy(player, stack.getCount());
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

    public List<ItemStack> getAvailableResults() {
        return this.availableResults;
    }

    public int getAvailableRecipeCount() {
        return this.availableResults.size();
    }

    public void setAvailableResults(List<ItemStack> results) {
        this.availableResults = results;
        this.contentsChangedListener.run();
    }

    public boolean canCraft() {
        return this.inputSlot.hasItem() && !this.availableResults.isEmpty();
    }

    @Override
    public boolean stillValid(Player player) {
        return AbstractContainerMenu.stillValid(this.context, player, TGBlocks.OSSUARY.get());
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (this.isInBounds(id)) {
            this.selectedRecipe.set(id);
            this.populateResult();
        }

        return true;
    }

    private boolean isInBounds(int id) {
        return id >= 0 && id < this.availableResults.size();
    }

    @Override
    public void slotsChanged(Container inventory) {
        ItemStack itemStack = this.inputSlot.getItem();

        if (!itemStack.is(this.inputStack.getItem())) {
            this.inputStack = itemStack.copy();
            this.updateInput(inventory, itemStack);
        }

    }

    private void updateInput(Container input, ItemStack stack) {
        if (!(this.world instanceof ServerLevel serverLevel)) {
            return;
        }

        this.selectedRecipe.set(-1);
        this.outputSlot.setByPlayer(ItemStack.EMPTY);
        this.availableRecipes = stack.isEmpty() ? Lists.newArrayList() : findRecipes(serverLevel, stack);
        this.availableResults = this.availableRecipes.stream()
                .map(recipe -> recipe.value().getOutput().copy())
                .toList();

        if (this.player instanceof ServerPlayer serverPlayer) {
            OssuaryRecipesSync.send(serverPlayer, this.containerId, this.availableResults);
        }
    }

    private static List<RecipeHolder<OssuaryRecipe>> findRecipes(ServerLevel level, ItemStack stack) {
        SingleRecipeInput recipeInput = new SingleRecipeInput(stack);
        List<RecipeHolder<OssuaryRecipe>> found = Lists.newArrayList();

        for (RecipeHolder<?> holder : level.recipeAccess().getRecipes()) {
            if (holder.value() instanceof OssuaryRecipe recipe && recipe.matches(recipeInput, level)) {
                found.add(new RecipeHolder<>(holder.id(), recipe));
            }
        }

        found.sort(Comparator.comparing(holder -> holder.value().getOutput().getItem().getDescriptionId()));
        return found;
    }

    void populateResult() {
        if (!(this.world instanceof ServerLevel)) {
            return;
        }

        if (!this.availableRecipes.isEmpty() && this.isInBounds(this.selectedRecipe.get())) {
            RecipeHolder<OssuaryRecipe> carvingRecipe = this.availableRecipes.get(this.selectedRecipe.get());
            ItemStack itemStack = carvingRecipe.value().assemble(new SingleRecipeInput(this.input.getItem(0)));

            if (itemStack.isItemEnabled(this.world.enabledFeatures())) {
                this.output.setRecipeUsed(carvingRecipe);
                this.outputSlot.setByPlayer(itemStack);
            } else {
                this.outputSlot.setByPlayer(ItemStack.EMPTY);
            }
        } else {
            this.outputSlot.setByPlayer(ItemStack.EMPTY);
        }

        this.broadcastChanges();
    }

    @Override
    public MenuType<?> getType() {
        return TGScreens.OSSUARY_SCREEN_HANDLER;
    }

    public void setContentsChangedListener(Runnable contentsChangedListener) {
        this.contentsChangedListener = contentsChangedListener;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return slot.container != this.output && super.canTakeItemForPickAll(stack, slot);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = (Slot)this.slots.get(slot);

        if (slot2 != null && slot2.hasItem()) {
            ItemStack itemStack2 = slot2.getItem();
            Item item = itemStack2.getItem();
            itemStack = itemStack2.copy();

            if (slot == 1) {
                item.onCraftedBy(itemStack2, player);

                if (!this.moveItemStackTo(itemStack2, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }

                slot2.onQuickCraft(itemStack2, itemStack);
            } else if (slot == 0) {
                if (!this.moveItemStackTo(itemStack2, 2, 38, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.world instanceof ServerLevel serverLevel && !findRecipes(serverLevel, itemStack2).isEmpty()) {
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
                slot2.setByPlayer(ItemStack.EMPTY);
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

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.output.removeItemNoUpdate(1);
        this.context.execute((world, pos) -> {
            this.clearContainer(player, this.input);
        });
    }
}
