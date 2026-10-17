package com.lion.graveyard.mixin;

import com.lion.graveyard.util.StaffRecipeLock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CraftingResultSlot.class)
public abstract class CraftingResultSlotMixin extends Slot {
    protected CraftingResultSlotMixin(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canTakeItems(PlayerEntity player) {
        return !StaffRecipeLock.blocksTake(player, this.inventory) && super.canTakeItems(player);
    }
}
