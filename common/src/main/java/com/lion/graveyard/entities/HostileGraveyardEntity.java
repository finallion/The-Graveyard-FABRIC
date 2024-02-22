package com.lion.graveyard.entities;

import com.lion.graveyard.Graveyard;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class HostileGraveyardEntity extends Monster {
    private String name;
    private static final EntityDataAccessor<Boolean> CAN_BURN_IN_SUNLIGHT;

    public HostileGraveyardEntity(EntityType<? extends Monster> entityType, Level world, String name) {
        super(entityType, world);
        this.name = name;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(CAN_BURN_IN_SUNLIGHT, true);
        super.defineSynchedData();
    }

    @Override
    protected boolean isSunBurnTick() {
        return super.isSunBurnTick();
    }

    protected boolean isSunSensitive() {
        return true;
    }

    private boolean canBurnInSunlight() {
        return entityData.get(CAN_BURN_IN_SUNLIGHT);
    }

    public void setCanBurnInSunlight(boolean bool) {
        entityData.set(CAN_BURN_IN_SUNLIGHT, bool);
    }


    // on game stop
    public void addAdditionalSaveData(CompoundTag nbt) {
        nbt.putBoolean("canBurn", canBurnInSunlight());
        super.addAdditionalSaveData(nbt);
    }

    // on game load
    public void readAdditionalSaveData(CompoundTag nbt) {
        if (!nbt.contains("canBurn")) {
            this.setCanBurnInSunlight(canBurnInSunlight());
        } else {
            this.setCanBurnInSunlight(nbt.getBoolean("canBurn"));
        }

        super.readAdditionalSaveData(nbt);
    }

    public boolean canBeAffected(MobEffectInstance effect) {
        if (effect.getEffect() == MobEffects.WITHER) {
            return Graveyard.getConfig().mobConfigEntries.get(name).canBeWithered;
        }

        return super.canBeAffected(effect);
    }

    @Override
    public void aiStep() {
        if (this.isAlive()) {
            boolean bl = this.isSunSensitive() && this.isSunBurnTick() && Graveyard.getConfig().mobConfigEntries.get(name).canBurnInSunlight && canBurnInSunlight();
            if (bl) {
                ItemStack itemStack = this.getItemBySlot(EquipmentSlot.HEAD);
                if (!itemStack.isEmpty()) {
                    if (itemStack.isDamageableItem()) {
                        itemStack.setDamageValue(itemStack.getDamageValue() + this.random.nextInt(2));
                        if (itemStack.getDamageValue() >= itemStack.getMaxDamage()) {
                            this.broadcastBreakEvent(EquipmentSlot.HEAD);
                            this.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                        }
                    }

                    bl = false;
                }

                if (bl) {
                    this.setSecondsOnFire(8);
                }
            }
        }
        super.aiStep();
    }


    static {
        CAN_BURN_IN_SUNLIGHT = SynchedEntityData.defineId(HostileGraveyardEntity.class, EntityDataSerializers.BOOLEAN);
    }



}
