package com.lion.graveyard.entities;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.init.TGCriteria;
import com.lion.graveyard.init.TGSounds;
import com.lion.graveyard.item.DaggerItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class AcolyteEntity extends CorruptedIllager {

    public AcolyteEntity(EntityType<? extends CorruptedIllager> entityType, Level world) {
        super(entityType, world, "acolyte");
    }

    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance localDifficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation(Graveyard.MOD_ID, "bone_dagger"))));
    }

    @Override
    public void playAmbientSound() {
        this.playSound(TGSounds.ACOLYTE_AMBIENT.get(), 1.0F, 0.75F);
    }

    @Override
    protected void playHurtSound(DamageSource source) {
        this.playSound(TGSounds.ACOLYTE_HURT.get(), 1.0F, 0.75F);
    }

    protected SoundEvent getDeathSound() {
        return TGSounds.ACOLYTE_DEATH.get();
    }

    @Override
    public float getVoicePitch() {
        return 0.75F;
    }

    @Override
    protected void createWitherRose(@Nullable LivingEntity adversary) {
        if (adversary instanceof ServerPlayer player) {
            if (player.getMainHandItem().getItem() instanceof DaggerItem) {
                TGCriteria.KILLED_BY_BONE_DAGGER.get().trigger(player);
            }
        }

        super.createWitherRose(adversary);
    }
}
