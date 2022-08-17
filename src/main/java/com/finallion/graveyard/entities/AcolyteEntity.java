package com.finallion.graveyard.entities;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.init.TGAdvancements;
import com.finallion.graveyard.item.DaggerItem;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.NavigationConditions;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AcolyteEntity extends CorruptedIllager {
    public AcolyteEntity(EntityType<? extends CorruptedIllager> entityType, World world) {
        super(entityType, world, "acolyte");
    }


    protected void initEquipment(LocalDifficulty difficulty) {
        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Registry.ITEM.get(new Identifier(TheGraveyard.MOD_ID, "bone_dagger"))));
    }
    
    @Override
    public void playAmbientSound() {
        this.playSound(SoundEvents.ENTITY_VINDICATOR_AMBIENT, 1.0F, 1.0F);
    }

    @Override
    protected void playHurtSound(DamageSource source) {
        this.playSound(SoundEvents.ENTITY_VINDICATOR_HURT, 1.0F, 1.0F);
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        this.playSound(SoundEvents.ENTITY_VINDICATOR_DEATH, 1.0F, 1.0F);
    }

    @Override
    protected void onKilledBy(@Nullable LivingEntity adversary) {
        if (adversary instanceof ServerPlayerEntity player) {
            if (player.getMainHandStack().getItem() instanceof DaggerItem) {
                TGAdvancements.KILLED_BY_BONE_DAGGER.trigger(player);
            }
        }

        super.onKilledBy(adversary);
    }
}
