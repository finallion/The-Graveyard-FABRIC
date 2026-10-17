package com.lion.graveyard.mixin.client;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractContainerScreen.class)
public interface HandledScreenAccessor {
    @Accessor("leftPos")
    int graveyard$getX();

    @Accessor("topPos")
    int graveyard$getY();

    @Accessor("imageWidth")
    int graveyard$getBackgroundWidth();
}
