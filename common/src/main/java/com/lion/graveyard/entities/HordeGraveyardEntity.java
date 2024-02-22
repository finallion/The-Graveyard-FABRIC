package com.lion.graveyard.entities;

import com.lion.graveyard.init.TGCriteria;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public abstract class HordeGraveyardEntity extends HostileGraveyardEntity {
    private BlockPos patrolTarget;
    private boolean patrolling;
    private boolean patrolLeader;

    protected HordeGraveyardEntity(EntityType<? extends Monster> entityType, Level world, String name) {
        super(entityType, world, name);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new HordeGraveyardEntity.PatrolGoal<>(this, 1.0D, 0.9D));
    }

    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        if (this.patrolTarget != null) {
            nbt.put("PatrolTarget", NbtUtils.writeBlockPos(this.patrolTarget));
        }

        nbt.putBoolean("PatrolLeader", this.patrolLeader);
        nbt.putBoolean("Patrolling", this.patrolling);
    }

    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("PatrolTarget")) {
            this.patrolTarget = NbtUtils.readBlockPos(nbt.getCompound("PatrolTarget"));
        }
        this.patrolLeader = nbt.getBoolean("PatrolLeader");
        this.patrolling = nbt.getBoolean("Patrolling");
    }

    public double getMyRidingOffset() {
        return -0.45D;
    }

    public boolean canBeLeader() {
        return true;
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType spawnReason, @Nullable SpawnGroupData entityData, @Nullable CompoundTag entityNbt) {
        if (spawnReason != MobSpawnType.PATROL && spawnReason != MobSpawnType.EVENT && spawnReason != MobSpawnType.STRUCTURE && this.random.nextFloat() < 0.06F && this.canBeLeader()) {
            this.patrolLeader = true;
        }

        if (spawnReason == MobSpawnType.PATROL) {
            this.patrolling = true;
        }

        return super.finalizeSpawn(world, difficulty, spawnReason, entityData, entityNbt);
    }

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
    public void setLastHurtByMob(@Nullable LivingEntity adversary) {
        if (adversary instanceof ServerPlayer player) {
            if (this.patrolLeader) {
                TGCriteria.KILL_HORDE.get().trigger(player);
            }
        }

        super.setLastHurtByMob(adversary);
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
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            boolean bl = this.entity.level().getGameTime() < this.nextPatrolSearchTime;
            return this.entity.isRaidCenterSet() && this.entity.getTarget() == null && !this.entity.isVehicle() && this.entity.hasPatrolTarget() && !bl;
        }

        public void start() {
        }

        public void stop() {
        }

        public void tick() {
            boolean bl = this.entity.isPatrolLeader();
            PathNavigation entityNavigation = this.entity.getNavigation();
            if (entityNavigation.isDone()) {
                List<HordeGraveyardEntity> list = this.findPatrolTargets();
                if (this.entity.isRaidCenterSet() && list.isEmpty()) {
                    this.entity.setPatrolling(false);
                } else if (bl && this.entity.getPatrolTarget().closerToCenterThan(this.entity.position(), 10.0D)) {
                    this.entity.setRandomPatrolTarget();
                } else {
                    Vec3 vec3d = Vec3.atBottomCenterOf(this.entity.getPatrolTarget());
                    Vec3 vec3d2 = this.entity.position();
                    Vec3 vec3d3 = vec3d2.subtract(vec3d);
                    vec3d = vec3d3.yRot(90.0F).scale(0.4D).add(vec3d);
                    Vec3 vec3d4 = vec3d.subtract(vec3d2).normalize().scale(10.0D).add(vec3d2);
                    BlockPos blockPos = BlockPos.containing(vec3d4);
                    blockPos = this.entity.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockPos);
                    if (!entityNavigation.moveTo((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), bl ? this.followSpeed : this.leaderSpeed)) {
                        this.wander();
                        this.nextPatrolSearchTime = this.entity.level().getGameTime() + 200L;
                    } else if (bl) {
                        Iterator var9 = list.iterator();
                        while(var9.hasNext()) {
                            HordeGraveyardEntity patrolEntity = (HordeGraveyardEntity)var9.next();
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
}
