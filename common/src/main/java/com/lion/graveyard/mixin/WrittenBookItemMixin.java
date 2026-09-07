package com.lion.graveyard.mixin;

import com.lion.graveyard.chronicle.Chronicle;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/// The lore books that already lie in the structures count as found pages when they are opened.
@Mixin(WrittenBookItem.class)
public abstract class WrittenBookItemMixin {
    @Inject(method = "use", at = @At("HEAD"))
    private void graveyard$findPage(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        if (user instanceof ServerPlayerEntity player) {
            String page = Chronicle.pageOf(user.getStackInHand(hand));
            if (page != null) Chronicle.find(player, page);
        }
    }
}
