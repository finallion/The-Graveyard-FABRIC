package com.finallion.graveyard.entities;

import com.finallion.graveyard.TheGraveyard;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class CorruptedPillager extends CorruptedIllager {
    private AttributeContainer attributeContainer;

    public CorruptedPillager(EntityType<? extends CorruptedIllager> entityType, World world) {
        super(entityType, world);
    }

    public boolean canHaveStatusEffect(StatusEffectInstance effect) {
        if (effect.getEffectType() == StatusEffects.WITHER) {
            if (TheGraveyard.config.mobConfigEntries.get("corrupted_pillager").canBeWithered) {
                return true;
            } else {
                return false;
            }
        }

        return super.canHaveStatusEffect(effect);
    }


    @Override
    public void tickMovement() {
        if (this.isAlive()) {
            boolean bl = this.burnsInDaylight() && this.isAffectedByDaylight() && TheGraveyard.config.mobConfigEntries.get("corrupted_pillager").canBurnInSunlight;
            if (bl) {
                ItemStack itemStack = this.getEquippedStack(EquipmentSlot.HEAD);
                if (!itemStack.isEmpty()) {
                    if (itemStack.isDamageable()) {
                        itemStack.setDamage(itemStack.getDamage() + this.random.nextInt(2));
                        if (itemStack.getDamage() >= itemStack.getMaxDamage()) {
                            this.sendEquipmentBreakStatus(EquipmentSlot.HEAD);
                            this.equipStack(EquipmentSlot.HEAD, ItemStack.EMPTY);
                        }
                    }

                    bl = false;
                }

                if (bl) {
                    this.setOnFireFor(8);
                }
            }
        }

        super.tickMovement();
    }


    @Override
    public AttributeContainer getAttributes() {
        if(attributeContainer == null)
            attributeContainer = new AttributeContainer(HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3499999940395355D).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 12.0D).add(EntityAttributes.GENERIC_MAX_HEALTH, 24.0D).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0D).build());
        return attributeContainer;
    }

    @Override
    public CorruptedIllager.State getState() {
        if (this.isAttacking()) {
            return CorruptedIllager.State.ATTACKING;
        } else {
            return State.UNDEAD;
        }
    }

}
