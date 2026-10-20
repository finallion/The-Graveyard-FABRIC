package com.lion.graveyard.mixin;

import com.lion.graveyard.init.TGAdvancements;
import com.lion.graveyard.init.TGTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

@Mixin(InventoryChangeTrigger.class)
public abstract class InventoryChangedCriterionMixin {
    private static final int DISTINCT_ITEMS_NEEDED = 10;

    @Inject(method = "trigger(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/item/ItemStack;)V", at = @At("TAIL"))
    private void graveyard$countSkeletonDecoration(
            ServerPlayer player,
            Inventory inventory,
            ItemStack stack,
            CallbackInfo callback
    ) {
        if (stack.isEmpty() || !stack.is(TGTags.SKELETON_DECORATION)) {
            return;
        }

        Set<Item> distinct = new HashSet<>();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack held = inventory.getItem(slot);

            if (held.is(TGTags.SKELETON_DECORATION)) {
                distinct.add(held.getItem());
            }
        }

        if (distinct.size() >= DISTINCT_ITEMS_NEEDED) {
            TGAdvancements.BONE_COLLECTOR.trigger(player);
        }
    }
}
