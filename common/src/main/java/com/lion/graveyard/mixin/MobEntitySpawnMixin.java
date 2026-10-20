package com.lion.graveyard.mixin;

import com.lion.graveyard.entities.MobSpawnGate;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobEntitySpawnMixin {

    @Inject(method = "checkSpawnRules(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/entity/EntitySpawnReason;)Z", at = @At("HEAD"), cancellable = true)
    private void graveyard$gateAutomaticSpawn(
            LevelAccessor world,
            EntitySpawnReason spawnReason,
            CallbackInfoReturnable<Boolean> callback
    ) {
        Mob mob = (Mob) (Object) this;

        if (MobSpawnGate.blocks(mob.getType(), spawnReason) || MobSpawnGate.inForbiddenBiome(world, mob.blockPosition(), mob.getType(), spawnReason)) {
            callback.setReturnValue(false);
        }
    }

    // Structure entities and spawners with custom rules never call checkSpawnRules.
    @Inject(method = "finalizeSpawn", at = @At("HEAD"))
    private void graveyard$discardGatedStructureMob(
            ServerLevelAccessor world,
            DifficultyInstance difficulty,
            EntitySpawnReason spawnReason,
            @Nullable SpawnGroupData entityData,
            CallbackInfoReturnable<SpawnGroupData> callback
    ) {
        Mob mob = (Mob) (Object) this;

        if ((spawnReason == EntitySpawnReason.STRUCTURE || spawnReason == EntitySpawnReason.SPAWNER) && MobSpawnGate.blocks(mob.getType(), spawnReason)) {
            mob.discard();
        }
    }
}
