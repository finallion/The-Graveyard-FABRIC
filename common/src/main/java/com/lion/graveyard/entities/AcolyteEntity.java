package com.lion.graveyard.entities;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.init.TGCriteria;
import com.lion.graveyard.init.TGSounds;
import com.lion.graveyard.item.DaggerItem;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AcolyteEntity extends CorruptedIllager {

    public AcolyteEntity(EntityType<? extends CorruptedIllager> entityType, Level world) {
        super(entityType, world, "acolyte");
    }

    protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Registries.ITEM.get(new Identifier(Graveyard.MOD_ID, "bone_dagger"))));
    }

    @Override
    public void playAmbientSound() {
        this.playSound(TGSounds.ACOLYTE_AMBIENT.get(), 1.0F, 0.75F);
    }

    @Override
    protected void playHurtSound(DamageSource source) {
        this.playSound(TGSounds.ACOLYTE_HURT.get(), 1.0F, 0.75F);
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        this.playSound(TGSounds.ACOLYTE_DEATH.get(), 1.0F, 0.75F);
    }

    @Override
    protected void onKilledBy(@Nullable LivingEntity adversary) {
        if (adversary instanceof ServerPlayerEntity player) {
            if (player.getMainHandStack().getItem() instanceof DaggerItem) {
                TGCriteria.KILLED_BY_BONE_DAGGER.get().trigger(player);
            }
        }

        super.onKilledBy(adversary);
    }
}
