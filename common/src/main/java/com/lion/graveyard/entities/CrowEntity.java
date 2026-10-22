package com.lion.graveyard.entities;

import com.lion.graveyard.entities.ai.goals.CircleAnchorGoal;
import com.lion.graveyard.entities.ai.goals.FlockStartleGoal;
import com.lion.graveyard.entities.ai.goals.JoinFlockGoal;
import com.lion.graveyard.entities.ai.goals.SettleGoal;
import com.lion.graveyard.init.TGSounds;
import com.lion.graveyard.init.TGTags;
import com.lion.graveyard.util.CrowAnchors;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

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

    public CrowEntity(EntityType<? extends CorvidEntity> type, Level world) {
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
    public int getMaxSpawnClusterSize() {
        return MAX_FLOCK;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new FlockStartleGoal(this));
        this.goalSelector.addGoal(2, new CircleAnchorGoal(this));
        this.goalSelector.addGoal(3, new JoinFlockGoal(this));
        this.goalSelector.addGoal(4, new SettleGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
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
        return this.hasLeader() && this.distanceToSqr(this.leader) <= CLOSE_TO_LEADER_SQ;
    }

    public void moveTowardLeader() {
        if (this.hasLeader()) {
            this.takeOff();
            this.getNavigation().moveTo(this.leader, 1.0D);
        }
    }

    /// A leader never joins another flock. This prevents leader chains.
    public void findFlock() {
        if (this.hasLeader() || this.groupSize > 1) {
            return;
        }

        List<CrowEntity> others = this.level().getEntitiesOfClass(
                CrowEntity.class,
                this.getBoundingBox().inflate(FLOCK_SEARCH_RADIUS),
                other -> other != this && other.isAlive()
        );
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

    public int getFlockSize() {
        return this.getFlockHead().groupSize;
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
        this.playSound(TGSounds.CROW_ALARM.get(), 0.7F, 0.9F + this.getRandom().nextFloat() * 0.2F);

        CrowEntity head = this.getFlockHead();
        List<CrowEntity> flock = this.level().getEntitiesOfClass(
                CrowEntity.class,
                this.getBoundingBox().inflate(ALARM_SPREAD_RADIUS),
                other -> other != this && other.isAlive() && other.getFlockHead() == head
        );
        for (CrowEntity other : flock) {
            if (other.alarm == 0 && other.alarmDelay == 0) {
                other.alarmDelay = 1 + Mth.floor(this.distanceTo(other) * ALARM_TICKS_PER_BLOCK);
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
        if ((this.tickCount + this.getId()) % ANCHOR_INTERVAL != 0) {
            return;
        }

        this.anchor = CrowAnchors.find((ServerLevel) this.level(), this.blockPosition());

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
    public int getAmbientSoundInterval() {
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
