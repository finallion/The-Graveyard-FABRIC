package com.finallion.graveyard.entities;

import com.finallion.graveyard.TheGraveyard;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.NavigationConditions;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.AttributeContainer;
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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AcolyteEntity extends CorruptedIllager implements Burnable {
    private AttributeContainer attributeContainer;

    public AcolyteEntity(EntityType<? extends CorruptedIllager> entityType, World world) {
        super(entityType, world);
    }

    public boolean canHaveStatusEffect(StatusEffectInstance effect) {
        if (effect.getEffectType() == StatusEffects.WITHER) {
            if (TheGraveyard.config.mobConfigEntries.get("acolyte").canBeWithered) {
                return true;
            } else {
                return false;
            }
        }

        return super.canHaveStatusEffect(effect);
    }

    protected void initEquipment(LocalDifficulty difficulty) {
        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Registry.ITEM.get(new Identifier(TheGraveyard.MOD_ID, "bone_dagger"))));
    }


    @Override
    public void tickMovement() {
        if (this.isAlive()) {
            boolean bl = this.burnsInDaylight() && this.isAffectedByDaylight() && TheGraveyard.config.mobConfigEntries.get("acolyte").canBurnInSunlight;
            canBurnInSunlight(bl);
        }

        super.tickMovement();
    }


    @Override
    public AttributeContainer getAttributes() {
        if(attributeContainer == null)
            attributeContainer = new AttributeContainer(HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3499999940395355D).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 12.0D).add(EntityAttributes.GENERIC_MAX_HEALTH, 24.0D).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0D).build());
        return attributeContainer;
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
    public void canBurnInSunlight(boolean canBurn) {
        if (canBurn) {
            ItemStack itemStack = this.getEquippedStack(EquipmentSlot.HEAD);
            if (!itemStack.isEmpty()) {
                if (itemStack.isDamageable()) {
                    itemStack.setDamage(itemStack.getDamage() + this.random.nextInt(2));
                    if (itemStack.getDamage() >= itemStack.getMaxDamage()) {
                        this.sendEquipmentBreakStatus(EquipmentSlot.HEAD);
                        this.equipStack(EquipmentSlot.HEAD, ItemStack.EMPTY);
                    }
                }

                canBurn = false;
            }

            if (canBurn) {
                this.setOnFireFor(8);
            }
        }
    }
}
