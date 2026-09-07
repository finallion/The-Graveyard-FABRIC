package com.lion.graveyard.entities.ai.goals;

import com.lion.graveyard.entities.CorvidEntity;
import com.lion.graveyard.init.TGTags;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.goal.FlyGoal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/// The parrot's fly-onto-tree wander, aimed at gravestones and fences instead of leaves. A perched
/// bird mostly stays put, so the flock does not churn in front of the player.
public class FlyOntoPerchGoal extends FlyGoal {
    private static final int SEARCH_RADIUS = 8;
    private static final int STAY_PERCHED_CHANCE = 6;

    private final CorvidEntity corvid;

    public FlyOntoPerchGoal(CorvidEntity corvid, double speed) {
        super(corvid, speed);
        this.corvid = corvid;
    }

    @Override
    public boolean canStart() {
        if (this.corvid.isPerched() && this.corvid.getRandom().nextInt(STAY_PERCHED_CHANCE) != 0) {
            return false;
        }
        return super.canStart();
    }

    @Nullable
    @Override
    protected Vec3d getWanderTarget() {
        Vec3d target = null;
        if (this.mob.isTouchingWater()) {
            target = FuzzyTargeting.find(this.mob, 15, 15);
        }
        if (this.mob.getRandom().nextFloat() >= this.probability) {
            target = this.getPerchTarget();
        }
        return target == null ? super.getWanderTarget() : target;
    }

    @Nullable
    private Vec3d getPerchTarget() {
        World world = this.mob.getWorld();
        BlockPos own = this.mob.getBlockPos();
        BlockPos.Mutable above = new BlockPos.Mutable();
        BlockPos.Mutable below = new BlockPos.Mutable();

        for (BlockPos pos : BlockPos.iterate(
                MathHelper.floor(this.mob.getX() - SEARCH_RADIUS), MathHelper.floor(this.mob.getY() - 6.0D), MathHelper.floor(this.mob.getZ() - SEARCH_RADIUS),
                MathHelper.floor(this.mob.getX() + SEARCH_RADIUS), MathHelper.floor(this.mob.getY() + 6.0D), MathHelper.floor(this.mob.getZ() + SEARCH_RADIUS))) {
            if (own.equals(pos)) {
                continue;
            }
            if (world.getBlockState(below.set(pos, Direction.DOWN)).isIn(TGTags.CORVID_PERCHES)
                    && world.isAir(pos) && world.isAir(above.set(pos, Direction.UP))) {
                return Vec3d.ofBottomCenter(pos);
            }
        }
        return null;
    }
}
