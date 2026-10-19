package com.lion.graveyard.entities;

import com.lion.graveyard.Graveyard;
import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.Identifier;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public abstract class AngerableGraveyardEntity extends HordeGraveyardEntity implements NeutralMob {
    private static final Identifier ATTACKING_SPEED_BOOST_ID = Graveyard.id("attacking_speed_boost_id");
    private static final AttributeModifier ATTACKING_SPEED_BOOST;
    private static final EntityDataAccessor<Boolean> ANGRY;
    private static final EntityDataAccessor<Boolean> PROVOKED;
    private static final UniformInt ANGER_TIME;
    private EntityReference<LivingEntity> target;
    private int ageWhenTargetSet;
    private long angerEndTime;

    public AngerableGraveyardEntity(EntityType<? extends Monster> entityType, Level world, String name) {
        super(entityType, world, name);
    }

    @Override
    public void aiStep() {
        if (!this.level().isClientSide()) {
            this.updatePersistentAnger((ServerLevel)this.level(), true);
        }

        super.aiStep();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);

        builder.define(ANGRY, false);
        builder.define(PROVOKED, false);
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        this.addPersistentAngerSaveData(output);
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.readPersistentAngerSaveData(this.level(), input);
    }

    @Override
    public void readPersistentAngerSaveData(Level level, ValueInput input) {
        NeutralMob.super.readPersistentAngerSaveData(level, input);
        LegacyEntityData.readLegacyAngerTarget(this, level, input);
    }

    @Override
    public boolean isAngry() {
        return this.entityData.get(ANGRY);
    }

    @Override
    public void setPersistentAngerEndTime(long endTime) {
        this.angerEndTime = endTime;
    }

    @Override
    public long getPersistentAngerEndTime() {
        return this.angerEndTime;
    }

    @Nullable
    @Override
    public EntityReference<LivingEntity> getPersistentAngerTarget() {
        return this.target;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable EntityReference<LivingEntity> reference) {
        this.target = reference;
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setTimeToRemainAngry(ANGER_TIME.sample(this.random));
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        super.setTarget(target);
        AttributeInstance entityAttributeInstance = this.getAttribute(Attributes.MOVEMENT_SPEED);

        if (target == null) {
            this.ageWhenTargetSet = 0;
            this.entityData.set(ANGRY, false);
            this.setTimeToRemainAngry(0);
            this.entityData.set(PROVOKED, false);
            entityAttributeInstance.removeModifier(ATTACKING_SPEED_BOOST);
        } else {
            this.ageWhenTargetSet = this.tickCount;
            this.entityData.set(ANGRY, true);

            if (!entityAttributeInstance.hasModifier(ATTACKING_SPEED_BOOST_ID)) {
                entityAttributeInstance.addTransientModifier(ATTACKING_SPEED_BOOST);
            }
        }
    }

    static {
        ATTACKING_SPEED_BOOST = new AttributeModifier(ATTACKING_SPEED_BOOST_ID, 0.15000000596046448D, AttributeModifier.Operation.ADD_VALUE);
        ANGRY = SynchedEntityData.defineId(AngerableGraveyardEntity.class, EntityDataSerializers.BOOLEAN);
        PROVOKED = SynchedEntityData.defineId(AngerableGraveyardEntity.class, EntityDataSerializers.BOOLEAN);
        ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    }



}
