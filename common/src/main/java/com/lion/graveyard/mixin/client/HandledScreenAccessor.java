package com.lion.graveyard.mixin.client;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HandledScreen.class)
public interface HandledScreenAccessor {
    @Accessor("x")
    int graveyard$getX();

    @Accessor("y")
    int graveyard$getY();

    @Accessor("backgroundWidth")
    int graveyard$getBackgroundWidth();
}
