package com.lion.graveyard.entities.renders;

import com.lion.graveyard.entities.CorruptedIllager;
import net.minecraft.client.renderer.entity.state.IllagerRenderState;

public class CorruptedIllagerRenderState extends IllagerRenderState {
    public CorruptedIllager.State state = CorruptedIllager.State.CROSSED;
    public boolean modelDamaged;
    public boolean mainHandEmpty;
    public boolean chasing;
}
