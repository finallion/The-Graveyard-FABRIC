package com.lion.graveyard.mixin;

import com.lion.graveyard.util.StaffRecipeLock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(CraftingScreenHandler.class)
public abstract class CraftingScreenHandlerMixin {
    @Inject(method = "updateResult", at = @At("HEAD"), cancellable = true)
    private static void graveyard$requireKnownStaffRecipe(ScreenHandler handler, World world, PlayerEntity player, RecipeInputInventory craftingInventory, CraftingResultInventory resultInventory, CallbackInfo info) {
        if (world.isClient || !(player instanceof ServerPlayerEntity serverPlayer)) {
            return;
        }

        Optional<CraftingRecipe> match = world.getServer().getRecipeManager().getFirstMatch(RecipeType.CRAFTING, craftingInventory, world);

        if (match.isEmpty() || !StaffRecipeLock.isLocked(serverPlayer, match.get())) {
            return;
        }

        resultInventory.setStack(0, ItemStack.EMPTY);
        handler.setPreviousTrackedSlot(0, ItemStack.EMPTY);
        serverPlayer.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(handler.syncId, handler.nextRevision(), 0, ItemStack.EMPTY));
        info.cancel();
    }

    @Inject(method = "quickMove", at = @At("HEAD"), cancellable = true)
    private void graveyard$blockLockedStaffShiftClick(PlayerEntity player, int slot, CallbackInfoReturnable<ItemStack> info) {
        CraftingScreenHandler self = (CraftingScreenHandler) (Object) this;

        if (slot == 0 && !self.slots.get(0).canTakeItems(player)) {
            info.setReturnValue(ItemStack.EMPTY);
        }
    }
}
