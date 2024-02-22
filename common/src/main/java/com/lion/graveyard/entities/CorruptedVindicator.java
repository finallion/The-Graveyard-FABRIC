package com.lion.graveyard.entities;


import com.lion.graveyard.init.TGSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;


public class CorruptedVindicator extends CorruptedIllager {

    public CorruptedVindicator(EntityType<? extends CorruptedIllager> entityType, Level world) {
        super(entityType, world, "corrupted_vindicator");
    }

    @Override
    public boolean isModelDamaged() {
        return true;
    }

    @Override
    public CorruptedIllager.State getState() {
        if (this.isAggressive()) {
            return State.UNDEAD_ATTACKING;
        } else {
            return State.UNDEAD;
        }
    }

    @Override
    public void playAmbientSound() {
        this.playSound(TGSounds.CORRUPTED_ILLAGER_AMBIENT.get(), 0.8F, 0.0F);
    }

    @Override
    protected void playHurtSound(DamageSource source) {
        this.playSound(TGSounds.CORRUPTED_ILLAGER_HURT.get(), 0.8F, 0.0F);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return TGSounds.CORRUPTED_ILLAGER_DEATH.get();
    }

    @Override
    public float getVoicePitch() {
        return 0.0F;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(TGSounds.CORRUPTED_ILLAGER_STEP.get(), 0.8F, 0.0F);
    }
}
