package com.lion.graveyard.mixin;

import com.lion.graveyard.init.TGAdvancements;
import com.lion.graveyard.init.TGTags;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

@Mixin(InventoryChangedCriterion.class)
public abstract class InventoryChangedCriterionMixin {
    private static final int DISTINCT_ITEMS_NEEDED = 10;

    @Inject(method = "trigger(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/item/ItemStack;)V", at = @At("TAIL"))
    private void graveyard$countSkeletonDecoration(ServerPlayerEntity player, PlayerInventory inventory, ItemStack stack, CallbackInfo callback) {
        if (stack.isEmpty() || !stack.isIn(TGTags.SKELETON_DECORATION)) {
            return;
        }

        Set<Item> distinct = new HashSet<>();
        for (int slot = 0; slot < inventory.size(); slot++) {
            ItemStack held = inventory.getStack(slot);

            if (held.isIn(TGTags.SKELETON_DECORATION)) {
                distinct.add(held.getItem());
            }
        }

        if (distinct.size() >= DISTINCT_ITEMS_NEEDED) {
            TGAdvancements.BONE_COLLECTOR.trigger(player);
        }
    }
}
