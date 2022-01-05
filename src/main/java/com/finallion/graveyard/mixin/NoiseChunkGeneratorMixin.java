package com.finallion.graveyard.mixin;


import com.finallion.graveyard.init.TGStructures;
import com.finallion.graveyard.world.structures.LargeGraveyardStructure;
import com.finallion.graveyard.world.structures.MediumGraveyardStructure;
import com.finallion.graveyard.world.structures.SmallDesertGraveyardStructure;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.gen.feature.NetherFortressFeature;
import net.minecraft.world.gen.feature.OceanMonumentFeature;
import net.minecraft.world.gen.feature.PillagerOutpostFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NoiseChunkGenerator.class)
public class NoiseChunkGeneratorMixin {

    @Inject(method = "getEntitySpawnList(Lnet/minecraft/world/biome/Biome;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/entity/SpawnGroup;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/collection/Pool;", at = @At(value = "HEAD"), cancellable = true)
    private void injectModSpawnPool(Biome biome, StructureAccessor accessor, SpawnGroup group, BlockPos pos, CallbackInfoReturnable<Pool<SpawnSettings.SpawnEntry>> cir) {
        Pool<SpawnSettings.SpawnEntry> pool = getStructureSpawns(biome, accessor, group, pos);
        if (pool != null) {
            cir.setReturnValue(pool);
        }
    }


    private static Pool<SpawnSettings.SpawnEntry> getStructureSpawns(Biome biome, StructureAccessor accessor, SpawnGroup group, BlockPos pos){
        if (group == SpawnGroup.MONSTER) {
            if (accessor.getStructureAt(pos, TGStructures.MEDIUM_GRAVEYARD_STRUCTURE).hasChildren()) {
                return MediumGraveyardStructure.MONSTER_SPAWNS;
            }

            if (accessor.getStructureAt(pos, TGStructures.SMALL_DESERT_GRAVEYARD_STRUCTURE).hasChildren()) {
                return SmallDesertGraveyardStructure.MONSTER_SPAWNS;
            }

            if (accessor.getStructureAt(pos, TGStructures.LARGE_GRAVEYARD_STRUCTURE).hasChildren()) {
                return LargeGraveyardStructure.MONSTER_SPAWNS;
            }
        }

        return null;
    }
}
