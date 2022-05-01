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
        super(entityType, world, "corrupted_pillager");
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

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        this.playSound(SoundEvents.ENTITY_PILLAGER_DEATH, 1.0F, -4.0F);
    }

}
