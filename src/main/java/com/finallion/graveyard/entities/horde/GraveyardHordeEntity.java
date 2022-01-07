package com.finallion.graveyard.entities.horde;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PatrolEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GraveyardHordeEntity extends HostileEntity {
    private BlockPos patrolTarget;
    private boolean patrolling;
    private boolean patrolLeader;

    protected GraveyardHordeEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(4, new GraveyardHordeEntity.PatrolGoal(this, 1.7D, 1.595D));
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.patrolTarget != null) {
            nbt.put("PatrolTarget", NbtHelper.fromBlockPos(this.patrolTarget));
        }

        nbt.putBoolean("PatrolLeader", this.patrolLeader);
        nbt.putBoolean("Patrolling", this.patrolling);
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("PatrolTarget")) {
            this.patrolTarget = NbtHelper.toBlockPos(nbt.getCompound("PatrolTarget"));
        }
        this.patrolLeader = nbt.getBoolean("PatrolLeader");
        this.patrolling = nbt.getBoolean("Patrolling");
    }

    public double getHeightOffset() {
        return -0.45D;
    }

    public boolean canLead() {
        return true;
    }

    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        if (spawnReason != SpawnReason.PATROL && spawnReason != SpawnReason.EVENT && spawnReason != SpawnReason.STRUCTURE && this.random.nextFloat() < 0.06F && this.canLead()) {
            this.patrolLeader = true;
        }

        if (this.isPatrolLeader()) {
            this.equipStack(EquipmentSlot.HEAD, Raid.getOminousBanner());
            this.setEquipmentDropChance(EquipmentSlot.HEAD, 2.0F);
        }

        if (spawnReason == SpawnReason.PATROL) {
            this.patrolling = true;
        }

        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    public boolean canImmediatelyDespawn(double distanceSquared) {
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
        this.patrolTarget = this.getBlockPos().add(-500 + this.random.nextInt(1000), 0, -500 + this.random.nextInt(1000));
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

    public static class PatrolGoal<T extends GraveyardHordeEntity> extends Goal {
        private static final int field_30474 = 200;
        private final T entity;
        private final double leaderSpeed;
        private final double followSpeed;
        private long nextPatrolSearchTime;

        public PatrolGoal(T entity, double leaderSpeed, double followSpeed) {
            this.entity = entity;
            this.leaderSpeed = leaderSpeed;
            this.followSpeed = followSpeed;
            this.nextPatrolSearchTime = -1L;
            this.setControls(EnumSet.of(Control.MOVE));
        }

        public boolean canStart() {
            boolean bl = this.entity.world.getTime() < this.nextPatrolSearchTime;
            return this.entity.isRaidCenterSet() && this.entity.getTarget() == null && !this.entity.hasPassengers() && this.entity.hasPatrolTarget() && !bl;
        }

        public void start() {
        }

        public void stop() {
        }

        public void tick() {
            boolean bl = this.entity.isPatrolLeader();
            EntityNavigation entityNavigation = this.entity.getNavigation();
            if (entityNavigation.isIdle()) {
                List<GraveyardHordeEntity> list = this.findPatrolTargets();
                if (this.entity.isRaidCenterSet() && list.isEmpty()) {
                    System.out.println("NO PATROL");
                    this.entity.setPatrolling(false);
                } else if (bl && this.entity.getPatrolTarget().isWithinDistance(this.entity.getPos(), 10.0D)) {
                    System.out.println("RANDOM PATROL");
                    this.entity.setRandomPatrolTarget();
                } else {
                    Vec3d vec3d = Vec3d.ofBottomCenter(this.entity.getPatrolTarget());
                    Vec3d vec3d2 = this.entity.getPos();
                    Vec3d vec3d3 = vec3d2.subtract(vec3d);
                    vec3d = vec3d3.rotateY(90.0F).multiply(0.4D).add(vec3d);
                    Vec3d vec3d4 = vec3d.subtract(vec3d2).normalize().multiply(10.0D).add(vec3d2);
                    BlockPos blockPos = new BlockPos(vec3d4);
                    blockPos = this.entity.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockPos);
                    if (!entityNavigation.startMovingTo((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), bl ? this.followSpeed : this.leaderSpeed)) {
                        System.out.println("WANDER");
                        this.wander();
                        this.nextPatrolSearchTime = this.entity.world.getTime() + 200L;
                    } else if (bl) {
                        Iterator var9 = list.iterator();
                        System.out.println("PATROL");
                        while(var9.hasNext()) {
                            GraveyardHordeEntity patrolEntity = (GraveyardHordeEntity)var9.next();
                            patrolEntity.setPatrolTarget(blockPos);
                        }
                    }
                }
            }

        }

        private List<GraveyardHordeEntity> findPatrolTargets() {
            return this.entity.world.getEntitiesByClass(GraveyardHordeEntity.class, this.entity.getBoundingBox().expand(16.0D), (patrolEntity) -> {
                return patrolEntity.hasNoRaid() && !patrolEntity.isPartOf(this.entity);
            });
        }

        private boolean wander() {
            Random random = this.entity.getRandom();
            BlockPos blockPos = this.entity.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, this.entity.getBlockPos().add(-8 + random.nextInt(16), 0, -8 + random.nextInt(16)));
            return this.entity.getNavigation().startMovingTo((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), this.leaderSpeed);
        }
    }



}
