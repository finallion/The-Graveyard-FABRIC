package com.lion.graveyard.entities;

import com.lion.graveyard.entities.ai.goals.CircleAnchorGoal;
import com.lion.graveyard.entities.ai.goals.FlockStartleGoal;
import com.lion.graveyard.entities.ai.goals.JoinFlockGoal;
import com.lion.graveyard.entities.ai.goals.SettleGoal;
import com.lion.graveyard.init.TGSounds;
import com.lion.graveyard.init.TGTags;
import com.lion.graveyard.util.CrowAnchors;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/// The flock is not saved to NBT. It forms again after a world load.
public class CrowEntity extends CorvidEntity {

    private static final int MAX_FLOCK = 12;
    private static final int FLOCK_SEARCH_RADIUS = 12;
    private static final int CLOSE_TO_LEADER_SQ = 12 * 12;

    private static final int ALARM_TICKS = 120;
    private static final int ALARM_SPREAD_RADIUS = 16;
    private static final float ALARM_TICKS_PER_BLOCK = 0.5F;

    private static final int ANCHOR_INTERVAL = 100;

    @Nullable
    private CrowEntity leader;
    private int groupSize = 1;
    private int alarm;
    private int alarmDelay;
    private boolean circling;
    @Nullable
    private CrowAnchors.Anchor anchor;
    private boolean gliding;

    public CrowEntity(EntityType<? extends CorvidEntity> type, World world) {
        super(type, world);
    }

    @Override
    public Species getSpecies() {
        return Species.CROW;
    }

    @Override
    public TagKey<Block> getPerchTag() {
        return TGTags.CROW_SETTLE;
    }

    @Override
    public int getLimitPerChunk() {
        return MAX_FLOCK;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new FlockStartleGoal(this));
        this.goalSelector.add(2, new CircleAnchorGoal(this));
        this.goalSelector.add(3, new JoinFlockGoal(this));
        this.goalSelector.add(4, new SettleGoal(this, 1.0D));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(6, new LookAroundGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient()) {
            return;
        }

        if (this.leader != null && !this.leader.isAlive()) {
            this.leaveFlock();
        }
        if (this.alarmDelay > 0 && --this.alarmDelay == 0) {
            this.alarm = ALARM_TICKS;
        }
        if (this.alarm > 0) {
            this.alarm--;
        }
        this.updateAnchor();
    }

    @Override
    public void remove(RemovalReason reason) {
        this.leaveFlock();
        super.remove(reason);
    }

    public boolean hasLeader() {
        return this.leader != null && this.leader.isAlive();
    }

    public CrowEntity getFlockHead() {
        return this.hasLeader() ? this.leader : this;
    }

    public boolean isSentinel() {
        return !this.hasLeader();
    }

    private boolean canAcceptMembers() {
        return !this.hasLeader() && this.groupSize < MAX_FLOCK;
    }

    public boolean isCloseToLeader() {
        return this.hasLeader() && this.squaredDistanceTo(this.leader) <= CLOSE_TO_LEADER_SQ;
    }

    public void moveTowardLeader() {
        if (this.hasLeader()) {
            this.takeOff();
            this.getNavigation().startMovingTo(this.leader, 1.0D);
        }
    }

    /// A leader never joins another flock. This prevents leader chains.
    public void findFlock() {
        if (this.hasLeader() || this.groupSize > 1) {
            return;
        }
        List<CrowEntity> others = this.getWorld().getEntitiesByClass(CrowEntity.class,
                this.getBoundingBox().expand(FLOCK_SEARCH_RADIUS), other -> other != this && other.isAlive());
        for (CrowEntity other : others) {
            CrowEntity head = other.getFlockHead();
            if (head != this && head.canAcceptMembers()) {
                this.leader = head;
                head.groupSize++;
                return;
            }
        }
    }

    private void leaveFlock() {
        if (this.leader != null) {
            this.leader.groupSize--;
            this.leader = null;
        }
    }

    public boolean isAlarmed() {
        return this.alarm > 0;
    }

    public void raiseAlarm() {
        if (this.alarm > 0) {
            return;
        }
        this.alarm = ALARM_TICKS;
        this.circling = false;
        this.playSound(TGSounds.CROW_ALARM.get(), 1.0F, 0.9F + this.getRandom().nextFloat() * 0.2F);

        CrowEntity head = this.getFlockHead();
        List<CrowEntity> flock = this.getWorld().getEntitiesByClass(CrowEntity.class,
                this.getBoundingBox().expand(ALARM_SPREAD_RADIUS),
                other -> other != this && other.isAlive() && other.getFlockHead() == head);
        for (CrowEntity other : flock) {
            if (other.alarm == 0 && other.alarmDelay == 0) {
                other.alarmDelay = 1 + MathHelper.floor(this.distanceTo(other) * ALARM_TICKS_PER_BLOCK);
            }
        }
    }

    public boolean isCircling() {
        return this.circling;
    }

    public void setCircling(boolean circling) {
        this.circling = circling;
    }

    @Nullable
    public CrowAnchors.Anchor getAnchor() {
        return this.getFlockHead().anchor;
    }

    private void updateAnchor() {
        if (this.hasLeader()) {
            this.anchor = null;
            return;
        }
        // The entity id offset spreads the scans of different flocks over ticks.
        if ((this.age + this.getId()) % ANCHOR_INTERVAL != 0) {
            return;
        }
        this.anchor = CrowAnchors.find((ServerWorld) this.getWorld(), this.getBlockPos());
        if (this.anchor == null) {
            this.circling = false;
        }
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return this.hasLeader() ? null : super.getAmbientSound();
    }

    @Override
    public int getMinAmbientSoundDelay() {
        return 300;
    }

    @Override
    public boolean isGliding() {
        return this.gliding;
    }

    public void setGliding(boolean gliding) {
        this.gliding = gliding;
    }

    @Override
    protected boolean canPeck() {
        return super.canPeck() && !this.isAlarmed();
    }
}
