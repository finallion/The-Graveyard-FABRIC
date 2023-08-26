package com.lion.graveyard.blockentities.animation;

import net.minecraft.util.math.MathHelper;

public class SarcophagusLidAnimator {
    private boolean open;
    private float progress;
    private float lastProgress;

    public SarcophagusLidAnimator() {
    }

    public void step() {
        this.lastProgress = this.progress;
        float f = 0.1F;
        if (!this.open && this.progress > 0.0F) {
            this.progress = Math.max(this.progress - 0.1F, 0.0F);
        } else if (this.open && this.progress < 1.0F) {
            this.progress = Math.min(this.progress + 0.1F, 1.0F);
        }

    }

    public float getProgress(float delta) {
        return MathHelper.lerp(delta, this.lastProgress, this.progress);
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
}
