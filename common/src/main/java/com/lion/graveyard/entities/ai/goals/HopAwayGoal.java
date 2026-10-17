package com.lion.graveyard.entities.ai.goals;

import com.lion.graveyard.entities.MagpieEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class HopAwayGoal extends HopGoal {
    private static final double PLAYER_RANGE = 6.0D;
    private static final int COOLDOWN = 60;
    private static final int FIND_ATTEMPTS = 4;
    private static final double MIN_DISTANCE = 3.0D;
    private static final double DISTANCE_SPREAD = 2.0D;
    private static final double ANGLE_SPREAD_DEGREES = 60.0D;

    @Nullable
    private Vec3 awayFrom;
    private int cooldown;

    public HopAwayGoal(MagpieEntity magpie) {
        super(magpie);
    }

    @Override
    public boolean canUse() {
        if (this.cooldown > 0) {
            this.cooldown--;
            return false;
        }

        if (!this.magpie.onGround()) {
            return false;
        }

        Player player = this.magpie.level().getNearestPlayer(this.magpie, PLAYER_RANGE);

        if (player == null || player.isShiftKeyDown() || player.isSpectator()) {
            return false;
        }

        this.awayFrom = player.position();
        this.target = this.findTarget();
        return this.target != null;
    }

    @Override
    @Nullable
    protected Vec3 findTarget() {
        if (this.awayFrom == null) {
            return null;
        }

        double baseAngle = Mth.atan2(this.magpie.getZ() - this.awayFrom.z, this.magpie.getX() - this.awayFrom.x);
        for (int i = 0; i < FIND_ATTEMPTS; i++) {
            double angle = baseAngle + Math.toRadians(this.magpie.getRandom().nextDouble() * ANGLE_SPREAD_DEGREES - ANGLE_SPREAD_DEGREES / 2.0D);
            Vec3 direction = new Vec3(Math.cos(angle), 0.0D, Math.sin(angle));

            if (this.isHopPathClear(direction)) {
                double distance = MIN_DISTANCE + this.magpie.getRandom().nextDouble() * DISTANCE_SPREAD;
                return new Vec3(this.magpie.getX() + direction.x * distance, this.magpie.getY(), this.magpie.getZ() + direction.z * distance);
            }
        }
        return null;
    }

    @Override
    protected int rollHopCount() {
        return 2 + this.magpie.getRandom().nextInt(3);
    }

    @Override
    public void stop() {
        super.stop();
        this.cooldown = COOLDOWN;
    }
}
