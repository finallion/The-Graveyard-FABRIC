package com.lion.graveyard.entities;


import com.lion.graveyard.init.TGSounds;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CorruptedVindicator extends AnimatedCorruptedIllager {

    public CorruptedVindicator(EntityType<? extends CorruptedIllager> entityType, World world) {
        this(entityType, world, "corrupted_vindicator");
    }

    protected CorruptedVindicator(EntityType<? extends CorruptedIllager> entityType, World world, String name) {
        super(entityType, world, name);
    }

    @Override
    protected boolean hasSpawnAnimation() {
        return true;
    }

    @Override
    public boolean isModelDamaged() {
        return true;
    }

    @Override
    public CorruptedIllager.State getState() {
        if (this.isAttacking()) {
            return State.UNDEAD_ATTACKING;
        } else {
            return State.UNDEAD;
        }
    }

    @Override
    public void playAmbientSound() {
    }

    @Override
    protected void playHurtSound(DamageSource source) {
        this.playSound(TGSounds.CORRUPTED_ILLAGER_HURT.get(), 0.8F, 0.0F);
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(TGSounds.CORRUPTED_ILLAGER_STEP.get(), 0.8F, 0.0F);
    }
}
