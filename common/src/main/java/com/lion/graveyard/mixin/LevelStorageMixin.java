package com.lion.graveyard.mixin;

import com.mojang.serialization.Dynamic;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/// Drop level.dat dimension entries without a dimension type, or the world fails to load.
@Mixin(LevelStorage.class)
public abstract class LevelStorageMixin {

    @ModifyVariable(method = "readGeneratorProperties", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private static Dynamic<?> graveyard$dropPastDimension(Dynamic<?> levelData) {
        return levelData.update("WorldGenSettings",
                settings -> settings.update("dimensions", dimensions -> dimensions.remove("graveyard:past")));
    }
}
