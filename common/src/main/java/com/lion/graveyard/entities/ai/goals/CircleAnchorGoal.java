package com.lion.graveyard.entities.ai.goals;

import com.lion.graveyard.entities.CrowEntity;
import com.lion.graveyard.util.CrowAnchors;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

/// Flies the ring through the move control. A path to a moving point never settles.
public class CircleAnchorGoal extends Goal {
    private static final double SPEED = 1.0D;
    /// Must stay below the top flight speed (about 0.27), or the ring shrinks.
    private static final float RING_SPEED = 0.2F;
    private static final int MIN_RADIUS = 8;
    private static final int RADIUS_SPREAD = 6;
    private static final int MIN_HEIGHT = 10;
    private static final int HEIGHT_SPREAD = 6;
    private static final int DURATION = 600;
    /// canStart runs every second tick, so this chance means about once per 24 seconds.
    private static final int START_CHANCE = 240;

    private final CrowEntity crow;
    private float phase;
    private int radius;
    private int height;
    private double ringY;
    private int direction;
    private int ticksLeft;
    private int stuckTicks;
    private boolean aborted;

    public CircleAnchorGoal(CrowEntity crow) {
        this.crow = crow;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.crow.isAlarmed() || this.crow.isPecking() || this.crow.getAnchor() == null) {
            return false;
        }

        CrowEntity head = this.crow.getFlockHead();

        if (head != this.crow) {
            return head.isCircling();
        }

        if (this.crow.getRandom().nextInt(START_CHANCE) != 0) {
            return false;
        }

        head.setCircling(true);
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.aborted || this.crow.isAlarmed() || this.crow.getAnchor() == null) {
            return false;
        }

        CrowEntity head = this.crow.getFlockHead();
        return head == this.crow ? this.ticksLeft > 0 : head.isCircling();
    }

    @Override
    public void start() {
        CrowAnchors.Anchor anchor = this.crow.getAnchor();

        if (anchor == null) {
            return;
        }

        BlockPos pos = anchor.pos();
        this.crow.takeOff();
        this.crow.getNavigation().stop();
        this.radius = MIN_RADIUS + this.crow.getRandom().nextInt(RADIUS_SPREAD);
        this.height = MIN_HEIGHT + this.crow.getRandom().nextInt(HEIGHT_SPREAD);
        this.direction = this.crow.getFlockHead().getId() % 2 == 0 ? 1 : -1;
        // Start at the bird's current angle, so the first tick is not a jump.
        this.phase = (float) Math.atan2(this.crow.getZ() - pos.getZ(), this.crow.getX() - pos.getX());
        this.ticksLeft = DURATION;
        this.stuckTicks = 0;
        this.aborted = false;
        this.ringY = Math.max(pos.getY() + this.height, this.findMaxTop(pos) + 3);
        this.crow.setGliding(true);
    }

    private int findMaxTop(BlockPos pos) {
        Level world = this.crow.level();
        int maxTop = world.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ());
        for (int k = 0; k < 8; k++) {
            float angle = k * (float) (Math.PI / 4.0D);
            int x = pos.getX() + Mth.floor(Mth.cos(angle) * this.radius);
            int z = pos.getZ() + Mth.floor(Mth.sin(angle) * this.radius);
            maxTop = Math.max(maxTop, world.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z));
        }
        return maxTop;
    }

    /// moveTo must run every tick. Otherwise the control goes to WAIT and the ring sags.
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        CrowAnchors.Anchor anchor = this.crow.getAnchor();

        if (anchor == null) {
            return;
        }

        BlockPos pos = anchor.pos();
        this.ticksLeft--;
        this.phase += RING_SPEED / this.radius * this.direction;

        if (this.crow.horizontalCollision) {
            this.stuckTicks++;
        } else {
            this.stuckTicks = 0;
        }

        if (this.stuckTicks >= 20) {
            this.aborted = true;
            return;
        }

        double x = pos.getX() + 0.5D + Mth.cos(this.phase) * this.radius;
        double z = pos.getZ() + 0.5D + Mth.sin(this.phase) * this.radius;
        this.crow.getMoveControl().setWantedPosition(x, this.ringY, z, SPEED);
    }

    @Override
    public void stop() {
        this.crow.setGliding(false);

        if (this.crow.getFlockHead() == this.crow) {
            this.crow.setCircling(false);
        }

        this.crow.getNavigation().stop();
    }
}
