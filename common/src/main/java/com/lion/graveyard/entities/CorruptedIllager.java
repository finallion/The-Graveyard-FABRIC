package com.lion.graveyard.entities;

import com.lion.graveyard.entities.ai.ChaseTarget;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

public abstract class CorruptedIllager extends HordeGraveyardEntity {
    private static final int CHASE_END_TICKS = 20;
    private static final EntityDataAccessor<Boolean> CHASING = SynchedEntityData.defineId(CorruptedIllager.class, EntityDataSerializers.BOOLEAN);

    private int ticksWithoutTarget;

    public CorruptedIllager(EntityType<? extends CorruptedIllager> entityType, Level world, String name) {
        super(entityType, world, name);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CHASING, false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new PatrolApproachGoal(this, 10.0F));
        this.goalSelector.addGoal(3, new AttackGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, Raider.class)).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, this.getWanderSpeed()));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    protected double getWanderSpeed() {
        return 0.6D;
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return target instanceof AbstractVillager && target.isBaby() ? false : super.canAttack(target);
    }

    @Override
    protected void customServerAiStep(ServerLevel serverLevel) {
        if (!this.isNoAi() && GoalUtils.hasGroundPathNavigation(this)) {
            boolean inRaid = ((ServerLevel)this.level()).isRaided(this.blockPosition());
            ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(inRaid);
        }

        trackChase();
        super.customServerAiStep(serverLevel);
    }

    /// The vanilla melee goal stops at each path end.
    private void trackChase() {
        if (ChaseTarget.isLive(this)) {
            this.ticksWithoutTarget = 0;
            setChasing(true);
        } else if (isChasing() && ++this.ticksWithoutTarget > CHASE_END_TICKS) {
            setChasing(false);
        }
    }

    public boolean isChasing() {
        return this.entityData.get(CHASING);
    }

    protected void setChasing(boolean chasing) {
        if (this.entityData.get(CHASING) != chasing) {
            this.entityData.set(CHASING, chasing);
        }
    }

    @Override
    public boolean canLead() {
        return false;
    }

    public static AttributeSupplier.Builder createCorruptedIllagerAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.3499999940395355D)
                .add(Attributes.FOLLOW_RANGE, 12.0D)
                .add(Attributes.MAX_HEALTH, 24.0D)
                .add(Attributes.ARMOR, 2.0D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D);
    }

    public State getState() {
        if (this.isChasing()) {
            return State.ATTACKING;
        } else {
            return State.CROSSED;
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(
            ServerLevelAccessor world,
            DifficultyInstance difficulty,
            EntitySpawnReason spawnReason,
            @Nullable SpawnGroupData entityData
    ) {
        SpawnGroupData entityData2 = super.finalizeSpawn(world, difficulty, spawnReason, entityData);
        ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
        RandomSource random = world.getRandom();
        this.populateDefaultEquipmentSlots(random, difficulty);
        this.populateDefaultEquipmentEnchantments(world, random, difficulty);
        return entityData2;
    }

    @Override
    protected boolean considersEntityAsAlly(Entity other) {
        if (super.considersEntityAsAlly(other)) {
            return true;
        } else if (other instanceof LivingEntity && other.is(EntityTypeTags.ILLAGER)) {
            return this.getTeam() == null && other.getTeam() == null;
        } else {
            return false;
        }
    }

    public boolean isModelDamaged() {
        return false;
    }

    class AttackGoal extends MeleeAttackGoal {
        public AttackGoal(CorruptedIllager illager) {
            super(illager, 1.0D, false);
        }

        @Override
        protected boolean canPerformAttack(LivingEntity entity) {
            if (this.mob.getVehicle() instanceof Ravager) {
                float f = this.mob.getVehicle().getBbWidth() - 0.1F;
                double reachSqr = f * 2.0F * f * 2.0F + entity.getBbWidth();

                return this.isTimeToAttack()
                        && this.mob.distanceToSqr(entity) <= reachSqr
                        && this.mob.getSensing().hasLineOfSight(entity);
            }

            return super.canPerformAttack(entity);
        }
    }

    public enum State {
        CROSSED,
        ATTACKING,
        SPELLCASTING,
        BOW_AND_ARROW,
        CROSSBOW_HOLD,
        CROSSBOW_CHARGE,
        CELEBRATING,
        UNDEAD,
        UNDEAD_ATTACKING,
        NEUTRAL;

        State() {}
    }

}

