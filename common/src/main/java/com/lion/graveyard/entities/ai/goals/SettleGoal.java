package com.lion.graveyard.entities.ai.goals;

import com.lion.graveyard.entities.CrowEntity;
import com.lion.graveyard.util.CrowAnchors;
import com.lion.graveyard.util.Scarecrows;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/// Searches around a perched flock member, so the flock lands in one place.
public class SettleGoal extends FlyOntoPerchGoal {
    private static final int DEATH_LANDING_RANGE_SQ = 6 * 6;
    private static final int DEATH_ANCHOR_RANGE_SQ = 24 * 24;

    private final CrowEntity crow;

    public SettleGoal(CrowEntity crow, double speed) {
        super(crow, speed);
        this.crow = crow;
    }

    @Override
    public boolean canUse() {
        return !this.crow.isAlarmed() && super.canUse();
    }

    @Override
    protected Vec3 getSearchCenter() {
        CrowAnchors.Anchor anchor = this.crow.getAnchor();

        if (anchor != null && anchor.kind() == CrowAnchors.Kind.DEATH
                && anchor.pos().distSqr(this.crow.blockPosition()) <= DEATH_ANCHOR_RANGE_SQ) {
            return Vec3.atCenterOf(anchor.pos());
        }

        CrowEntity head = this.crow.getFlockHead();
        return head != this.crow && head.isPerched() ? head.position() : super.getSearchCenter();
    }

    @Override
    protected boolean canPerchOn(Level world, BlockPos pos) {
        return super.canPerchOn(world, pos) || this.isNearDeathSite(world, pos);
    }

    @Override
    protected boolean isRepelled(Level world, BlockPos pos) {
        return Scarecrows.isNear(world, pos, Scarecrows.RADIUS);
    }

    @Override
    protected int scorePerch(BlockState ground, BlockPos pos) {
        return Math.max(super.scorePerch(ground, pos), this.isNearDeathSite(this.crow.level(), pos) ? 2 : 0);
    }

    private boolean isNearDeathSite(Level world, BlockPos pos) {
        CrowAnchors.Anchor anchor = this.crow.getAnchor();

        if (anchor == null || anchor.kind() != CrowAnchors.Kind.DEATH
                || anchor.pos().distSqr(pos) > DEATH_LANDING_RANGE_SQ) {
            return false;
        }

        return world.getBlockState(pos).isFaceSturdy(world, pos, Direction.UP);
    }
}
