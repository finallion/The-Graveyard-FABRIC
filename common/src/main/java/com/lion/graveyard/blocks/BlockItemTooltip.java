package com.lion.graveyard.blocks;

import java.util.function.Consumer;
import net.minecraft.network.chat.Component;

public interface BlockItemTooltip {
    void appendItemTooltip(Consumer<Component> tooltip);
}
