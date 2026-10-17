package com.lion.graveyard.entities.ai.goals;

import com.lion.graveyard.entities.CrowEntity;
import com.lion.graveyard.util.CrowAnchors;
import com.lion.graveyard.util.Scarecrows;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

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
    public boolean canStart() {
        return !this.crow.isAlarmed() && super.canStart();
    }

    @Override
    protected Vec3d getSearchCenter() {
        CrowAnchors.Anchor anchor = this.crow.getAnchor();

        if (anchor != null && anchor.kind() == CrowAnchors.Kind.DEATH
                && anchor.pos().getSquaredDistance(this.crow.getBlockPos()) <= DEATH_ANCHOR_RANGE_SQ) {
            return Vec3d.ofCenter(anchor.pos());
        }

        CrowEntity head = this.crow.getFlockHead();
        return head != this.crow && head.isPerched() ? head.getPos() : super.getSearchCenter();
    }

    @Override
    protected boolean canPerchOn(World world, BlockPos pos) {
        return super.canPerchOn(world, pos) || this.isNearDeathSite(world, pos);
    }

    @Override
    protected boolean isRepelled(World world, BlockPos pos) {
        return Scarecrows.isNear(world, pos, Scarecrows.RADIUS);
    }

    @Override
    protected int scorePerch(BlockState ground, BlockPos pos) {
        return Math.max(super.scorePerch(ground, pos), this.isNearDeathSite(this.crow.getWorld(), pos) ? 2 : 0);
    }

    private boolean isNearDeathSite(World world, BlockPos pos) {
        CrowAnchors.Anchor anchor = this.crow.getAnchor();

        if (anchor == null || anchor.kind() != CrowAnchors.Kind.DEATH
                || anchor.pos().getSquaredDistance(pos) > DEATH_LANDING_RANGE_SQ) {
            return false;
        }

        return world.getBlockState(pos).isSideSolidFullSquare(world, pos, Direction.UP);
    }
}
