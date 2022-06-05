package com.finallion.graveyard.mixin;


import net.minecraft.server.command.LocateCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LocateCommand.class)
public class LocateCommandMixin {
    @ModifyConstant(method = "executeLocateStructure(Lnet/minecraft/server/command/ServerCommandSource;Lnet/minecraft/command/argument/RegistryPredicateArgumentType$RegistryPredicate;)I", constant = @Constant(intValue = 100), require = 0)
    private static int increaseLocateCommandSearchRadius(int constant) {
        return 3000;
    }
}

