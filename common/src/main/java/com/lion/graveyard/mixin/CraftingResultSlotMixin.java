package com.lion.graveyard.mixin;

import com.lion.graveyard.util.StaffRecipeLock;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ResultSlot.class)
public abstract class CraftingResultSlotMixin extends Slot {
    protected CraftingResultSlotMixin(Container inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean mayPickup(Player player) {
        return !StaffRecipeLock.blocksTake(player, this.container) && super.mayPickup(player);
    }
}
