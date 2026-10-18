package com.lion.graveyard.mixin;

import com.lion.graveyard.util.StaffRecipeLock;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

@Mixin(CraftingMenu.class)
public abstract class CraftingScreenHandlerMixin {
    @Inject(method = "slotChangedCraftingGrid", at = @At("HEAD"), cancellable = true)
    private static void graveyard$requireKnownStaffRecipe(
            AbstractContainerMenu handler,
            ServerLevel world,
            Player player,
            CraftingContainer craftingInventory,
            ResultContainer resultInventory,
            @Nullable RecipeHolder<CraftingRecipe> lastRecipe,
            CallbackInfo info
    ) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        Optional<RecipeHolder<CraftingRecipe>> match = world.recipeAccess().getRecipeFor(RecipeType.CRAFTING, craftingInventory.asCraftInput(), world, lastRecipe);

        if (match.isEmpty() || !StaffRecipeLock.isLocked(serverPlayer, match.get())) {
            return;
        }

        resultInventory.setItem(0, ItemStack.EMPTY);
        handler.setRemoteSlot(0, ItemStack.EMPTY);
        serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(handler.containerId, handler.incrementStateId(), 0, ItemStack.EMPTY));
        info.cancel();
    }

    @Inject(method = "quickMoveStack", at = @At("HEAD"), cancellable = true)
    private void graveyard$blockLockedStaffShiftClick(Player player, int slot, CallbackInfoReturnable<ItemStack> info) {
        CraftingMenu self = (CraftingMenu) (Object) this;

        if (slot == 0 && !self.slots.get(0).mayPickup(player)) {
            info.setReturnValue(ItemStack.EMPTY);
        }
    }
}
