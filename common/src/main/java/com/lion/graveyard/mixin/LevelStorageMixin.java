package com.lion.graveyard.mixin;

import com.mojang.serialization.Dynamic;
import net.minecraft.util.filefix.fixes.LevelDatToSavedDataFileFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/// Strips the `graveyard:past` dimension from the world gen settings.
@Mixin(LevelDatToSavedDataFileFix.class)
public abstract class LevelStorageMixin {

    @ModifyVariable(method = "extractWorldGenSettingsToFile", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private Dynamic<?> graveyard$dropPastDimension(Dynamic<?> levelData) {
        return levelData.update(
                "world_gen_settings",
                settings -> settings.update("dimensions", dimensions -> dimensions.remove("graveyard:past"))
        );
    }
}
