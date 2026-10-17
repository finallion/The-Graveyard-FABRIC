package com.lion.graveyard.mixin;

import com.lion.graveyard.entities.TormentedVillagerEntity;
import net.minecraft.block.entity.BellBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BellBlockEntity.class)
public abstract class BellBlockEntityMixin {
    private static final long SCAN_COOLDOWN_TICKS = 20L;

    @Unique
    private long graveyard$lastScanTime = Long.MIN_VALUE / 2;

    @Inject(method = "activate", at = @At("TAIL"))
    private void graveyard$redeemTormentedVillagers(Direction direction, CallbackInfo callback) {
        BlockEntity bell = (BlockEntity) (Object) this;
        if (bell.getWorld() instanceof ServerWorld world) {
            if (world.getTime() - this.graveyard$lastScanTime < SCAN_COOLDOWN_TICKS) {
                return;
            }
            this.graveyard$lastScanTime = world.getTime();
            TormentedVillagerEntity.onBellRung(world, bell.getPos());
        }
    }
}
