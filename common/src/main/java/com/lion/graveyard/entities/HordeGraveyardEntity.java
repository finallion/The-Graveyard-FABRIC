package com.lion.graveyard.entities;

import com.lion.graveyard.init.TGAdvancements;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public abstract class HordeGraveyardEntity extends HostileGraveyardEntity {
    private BlockPos patrolTarget;
    private boolean patrolling;
    private boolean patrolLeader;
    private boolean hordeCaptain;

    protected HordeGraveyardEntity(EntityType<? extends Monster> entityType, Level world, String name) {
        super(entityType, world, name);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new PatrolApproachGoal(this, 10.0F));
        this.goalSelector.addGoal(4, new HordeGraveyardEntity.PatrolGoal<>(this, 1.0D, 0.9D));
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);

        if (this.patrolTarget != null) {
            ValueOutput target = output.child("PatrolTarget");
            target.putInt("X", this.patrolTarget.getX());
            target.putInt("Y", this.patrolTarget.getY());
            target.putInt("Z", this.patrolTarget.getZ());
        }

        output.putBoolean("PatrolLeader", this.patrolLeader);
        output.putBoolean("HordeCaptain", this.hordeCaptain);
        output.putBoolean("Patrolling", this.patrolling);
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        Optional<int[]> coordinates = input.getIntArray("PatrolTarget");

        if (coordinates.isPresent()) {
            int[] values = coordinates.get();
            this.patrolTarget = values.length == 3 ? new BlockPos(values[0], values[1], values[2]) : null;
        } else {
            input.child("PatrolTarget").ifPresent(target -> {
                this.patrolTarget = new BlockPos(target.getIntOr("X", 0), target.getIntOr("Y", 0), target.getIntOr("Z", 0));
            });
        }

        this.patrolLeader = input.getBooleanOr("PatrolLeader", false);
        this.hordeCaptain = input.getBooleanOr("HordeCaptain", this.patrolLeader);
        this.patrolling = input.getBooleanOr("Patrolling", false);
    }

    public boolean canLead() {
        return true;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(
            ServerLevelAccessor world,
            DifficultyInstance difficulty,
            EntitySpawnReason spawnReason,
            @Nullable SpawnGroupData entityData
    ) {
        if (spawnReason != EntitySpawnReason.PATROL && spawnReason != EntitySpawnReason.EVENT && spawnReason != EntitySpawnReason.STRUCTURE && this.random.nextFloat() < 0.06F && this.canLead()) {
            this.patrolLeader = true;
        }

        if (spawnReason == EntitySpawnReason.PATROL) {
            this.patrolling = true;
        }

        return super.finalizeSpawn(world, difficulty, spawnReason, entityData);
    }

    @Override
    public boolean removeWhenFarAway(double distanceSquared) {
        return !this.patrolling || distanceSquared > 16384.0D;
    }

    public void setPatrolTarget(BlockPos targetPos) {
        this.patrolTarget = targetPos;
        this.patrolling = true;
    }

    public void setPatrolLeader(boolean patrolLeader) {
        this.patrolLeader = patrolLeader;
        this.patrolling = true;
    }

    public void setHordeCaptain(boolean hordeCaptain) {
        this.hordeCaptain = hordeCaptain;
    }

    public boolean isPatrolLeader() {
        return this.patrolLeader;
    }

    public BlockPos getPatrolTarget() {
        return this.patrolTarget;
    }

    public boolean hasPatrolTarget() {
        return this.patrolTarget != null;
    }

    public void setRandomPatrolTarget() {
        this.patrolTarget = this.blockPosition().offset(-500 + this.random.nextInt(1000), 0, -500 + this.random.nextInt(1000));
        this.patrolling = true;
    }

    protected void setPatrolling(boolean patrolling) {
        this.patrolling = patrolling;
    }

    public boolean hasNoRaid() {
        return true;
    }

    protected boolean isRaidCenterSet() {
        return this.patrolling;
    }

    @Override
    protected void createWitherRose(@Nullable LivingEntity adversary) {
        if (adversary instanceof ServerPlayer player) {
            if (this.hordeCaptain) {
                TGAdvancements.KILL_HORDE.trigger(player);
            }
        }

        super.createWitherRose(adversary);
    }


    public static class PatrolGoal<T extends HordeGraveyardEntity> extends Goal {
        private final T entity;
        private final double leaderSpeed;
        private final double followSpeed;
        private long nextPatrolSearchTime;

        public PatrolGoal(T entity, double leaderSpeed, double followSpeed) {
            this.entity = entity;

            if (entity instanceof CorruptedIllager) {
                this.leaderSpeed = leaderSpeed - 0.5;
                this.followSpeed = followSpeed - 0.5;
            } else {
                this.leaderSpeed = leaderSpeed;
                this.followSpeed = followSpeed;
            }

            this.nextPatrolSearchTime = -1L;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            boolean searchCoolingDown = this.entity.level().getGameTime() < this.nextPatrolSearchTime;
            return this.entity.isRaidCenterSet() && this.entity.getTarget() == null && !this.entity.isVehicle() && this.entity.hasPatrolTarget() && !searchCoolingDown;
        }

        @Override
        public void start() {
        }

        @Override
        public void stop() {
        }

        @Override
        public void tick() {
            boolean isLeader = this.entity.isPatrolLeader();
            PathNavigation entityNavigation = this.entity.getNavigation();

            if (entityNavigation.isDone()) {
                List<HordeGraveyardEntity> list = this.findPatrolTargets();

                if (this.entity.isRaidCenterSet() && list.isEmpty()) {
                    this.entity.setPatrolling(false);
                } else if (isLeader && this.entity.getPatrolTarget().closerToCenterThan(this.entity.position(), 10.0D)) {
                    this.entity.setRandomPatrolTarget();
                } else {
                    Vec3 vec3d = Vec3.atBottomCenterOf(this.entity.getPatrolTarget());
                    Vec3 vec3d2 = this.entity.position();
                    Vec3 vec3d3 = vec3d2.subtract(vec3d);
                    vec3d = vec3d3.yRot(90.0F).scale(0.4D).add(vec3d);
                    Vec3 vec3d4 = vec3d.subtract(vec3d2).normalize().scale(10.0D).add(vec3d2);
                    BlockPos blockPos = BlockPos.containing(vec3d4);
                    blockPos = this.entity.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockPos);

                    if (!entityNavigation.moveTo((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), isLeader ? this.followSpeed : this.leaderSpeed)) {
                        this.wander();
                        this.nextPatrolSearchTime = this.entity.level().getGameTime() + 200L;
                    } else if (isLeader) {
                        for (HordeGraveyardEntity patrolEntity : list) {
                            patrolEntity.setPatrolTarget(blockPos);
                        }
                    }
                }
            }

        }

        private List<HordeGraveyardEntity> findPatrolTargets() {
            return this.entity.level().getEntitiesOfClass(HordeGraveyardEntity.class, this.entity.getBoundingBox().inflate(16.0D), (patrolEntity) -> {
                return patrolEntity.hasNoRaid() && !patrolEntity.is(this.entity);
            });
        }

        private boolean wander() {
            Random random = new Random();
            BlockPos blockPos = this.entity.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, this.entity.blockPosition().offset(-8 + random.nextInt(16), 0, -8 + random.nextInt(16)));
            return this.entity.getNavigation().moveTo((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), this.leaderSpeed);
        }
    }

    protected class PatrolApproachGoal extends Goal {
        private final HordeGraveyardEntity hordeEntity;
        private final float squaredDistance;
        public final TargetingConditions closeRaiderPredicate = TargetingConditions.forNonCombat().range(8.0D).ignoreLineOfSight().ignoreInvisibilityTesting();

        public PatrolApproachGoal(HordeGraveyardEntity hordeEntity, float distance) {
            this.hordeEntity = hordeEntity;
            this.squaredDistance = distance * distance;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity livingEntity = this.hordeEntity.getLastHurtByMob();
            return this.hordeEntity.isRaidCenterSet() && this.hordeEntity.getTarget() != null && !this.hordeEntity.isAggressive() && (livingEntity == null || livingEntity.getType() != EntityType.PLAYER);
        }

        @Override
        public void start() {
            super.start();
            this.hordeEntity.getNavigation().stop();
            List<HordeGraveyardEntity> list = getServerLevel(this.hordeEntity).getNearbyEntities(HordeGraveyardEntity.class, this.closeRaiderPredicate, this.hordeEntity, this.hordeEntity.getBoundingBox().inflate(8.0D, 8.0D, 8.0D));

            for (HordeGraveyardEntity hordeEntityEntity : list) {
                hordeEntityEntity.setTarget(this.hordeEntity.getTarget());
            }

        }

        @Override
        public void stop() {
            super.stop();
            LivingEntity livingEntity = this.hordeEntity.getTarget();

            if (livingEntity != null) {
                List<HordeGraveyardEntity> list = getServerLevel(this.hordeEntity).getNearbyEntities(HordeGraveyardEntity.class, this.closeRaiderPredicate, this.hordeEntity, this.hordeEntity.getBoundingBox().inflate(8.0D, 8.0D, 8.0D));

                for (HordeGraveyardEntity hordeEntityEntity : list) {
                    hordeEntityEntity.setTarget(livingEntity);
                    hordeEntityEntity.setAggressive(true);
                }

                this.hordeEntity.setAggressive(true);
            }

        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity livingEntity = this.hordeEntity.getTarget();

            if (livingEntity != null) {
                if (this.hordeEntity.distanceToSqr(livingEntity) > (double)this.squaredDistance) {
                    this.hordeEntity.getLookControl().setLookAt(livingEntity, 30.0F, 30.0F);

                    if (this.hordeEntity.random.nextInt(50) == 0) {
                        this.hordeEntity.playAmbientSound();
                    }
                } else {
                    this.hordeEntity.setAggressive(true);
                }

                super.tick();
            }
        }
    }



}
