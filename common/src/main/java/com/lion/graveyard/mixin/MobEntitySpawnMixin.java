package com.lion.graveyard.mixin;

import com.lion.graveyard.entities.MobSpawnGate;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public abstract class MobEntitySpawnMixin {

    @Inject(method = "canSpawn(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/entity/SpawnReason;)Z", at = @At("HEAD"), cancellable = true)
    private void graveyard$gateAutomaticSpawn(WorldAccess world, SpawnReason spawnReason, CallbackInfoReturnable<Boolean> callback) {
        MobEntity mob = (MobEntity) (Object) this;
        if (MobSpawnGate.blocks(mob.getType(), spawnReason) || MobSpawnGate.inForbiddenBiome(world, mob.getBlockPos(), mob.getType(), spawnReason)) {
            callback.setReturnValue(false);
        }
    }

    // Structure entities and spawners with custom rules never call canSpawn.
    @Inject(method = "initialize", at = @At("HEAD"))
    private void graveyard$discardGatedStructureMob(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt, CallbackInfoReturnable<EntityData> callback) {
        MobEntity mob = (MobEntity) (Object) this;
        if ((spawnReason == SpawnReason.STRUCTURE || spawnReason == SpawnReason.SPAWNER) && MobSpawnGate.blocks(mob.getType(), spawnReason)) {
            mob.discard();
        }
    }
}
