package com.lion.graveyard.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class AngerableGraveyardEntity extends HordeGraveyardEntity implements NeutralMob {
    private static final UUID ATTACKING_SPEED_BOOST_ID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
    private static final AttributeModifier ATTACKING_SPEED_BOOST;
    private static final EntityDataAccessor<Boolean> ANGRY;
    private static final EntityDataAccessor<Boolean> PROVOKED;
    private static final UniformInt ANGER_TIME;
    private UUID target;
    private int ageWhenTargetSet;
    private int angerTime;

    public AngerableGraveyardEntity(EntityType<? extends Monster> entityType, Level world, String name) {
        super(entityType, world, name);
    }

    public void aiStep() {
        if (!this.level().isClientSide()) {
            this.updatePersistentAnger((ServerLevel) this.level(), true);
        }
        super.aiStep();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(ANGRY, false);
        this.entityData.define(PROVOKED, false);
    }

    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        this.addPersistentAngerSaveData(nbt);
    }

    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.readPersistentAngerSaveData(this.level(), nbt);
    }
    
    public boolean hasAngerTime() {
        return this.entityData.get(ANGRY);
    }

    public void setRemainingPersistentAngerTime(int ticks) {
        this.angerTime = ticks;
    }

    public int getRemainingPersistentAngerTime() {
        return this.angerTime;
    }

    @Nullable
    @Override
    public UUID getPersistentAngerTarget() {
        return this.target;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID uuid) {
        this.target = uuid;
    }

    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(ANGER_TIME.sample(this.random));
    }

    public void setTarget(@Nullable LivingEntity target) {
        super.setTarget(target);
        AttributeInstance entityAttributeInstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (target == null) {
            this.ageWhenTargetSet = 0;
            this.entityData.set(ANGRY, false);
            this.setRemainingPersistentAngerTime(0);
            this.entityData.set(PROVOKED, false);
            entityAttributeInstance.removeModifier(ATTACKING_SPEED_BOOST.getId());
        } else {
            this.ageWhenTargetSet = this.tickCount;
            this.entityData.set(ANGRY, true);
            if (!entityAttributeInstance.hasModifier(ATTACKING_SPEED_BOOST)) {
                entityAttributeInstance.addTransientModifier(ATTACKING_SPEED_BOOST);
            }
        }
    }

    static {
        ATTACKING_SPEED_BOOST = new AttributeModifier(ATTACKING_SPEED_BOOST_ID, "Attacking speed boost", 0.15000000596046448D, AttributeModifier.Operation.ADDITION);
        ANGRY = SynchedEntityData.defineId(AngerableGraveyardEntity.class, EntityDataSerializers.BOOLEAN);
        PROVOKED = SynchedEntityData.defineId(AngerableGraveyardEntity.class, EntityDataSerializers.BOOLEAN);
        ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    }



}
