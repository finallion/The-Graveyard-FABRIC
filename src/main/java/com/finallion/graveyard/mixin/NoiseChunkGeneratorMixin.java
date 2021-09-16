package com.finallion.graveyard.mixin;

import com.finallion.graveyard.init.TGStructures;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(NoiseChunkGenerator.class)
public class NoiseChunkGeneratorMixin {

    @Inject(at = @At("HEAD"), method = "getEntitySpawnList", cancellable = true)
    public void injectSpawnList(Biome biome, StructureAccessor accessor, SpawnGroup group, BlockPos pos, CallbackInfoReturnable<List<SpawnSettings.SpawnEntry>> info) {
        if (group == SpawnGroup.MONSTER) {
            if (accessor.getStructureAt(pos, false, TGStructures.MEDIUM_WALLED_GRAVEYARD).hasChildren()) {
                info.setReturnValue(TGStructures.MEDIUM_WALLED_GRAVEYARD.getMonsterSpawns());
            } else if (accessor.getStructureAt(pos, false, TGStructures.SMALL_WALLED_GRAVEYARD_DESERT).hasChildren()) {
                info.setReturnValue(TGStructures.SMALL_WALLED_GRAVEYARD_DESERT.getMonsterSpawns());
            }
        }
    }
}
