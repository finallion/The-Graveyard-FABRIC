package com.lion.graveyard.entities.ai.goals;

import com.lion.graveyard.entities.GhoulingEntity;
import java.util.EnumSet;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;

public class GhoulingCollectItemsGoal extends Goal {
    private static final double COLLECT_RANGE = 1.5D;
    private static final int MAX_TICKS = 200;

    private final GhoulingEntity mob;
    private final double speed;
    private ItemEntity item;
    private int orderedAge;

    public GhoulingCollectItemsGoal(GhoulingEntity mob, double speed) {
        this.mob = mob;
        this.speed = speed;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public void collect(ItemEntity item) {
        this.item = item;
        this.orderedAge = mob.tickCount;
    }

    @Override
    public boolean canUse() {
        return isValid();
    }

    @Override
    public boolean canContinueToUse() {
        return isValid();
    }

    private boolean isValid() {
        return item != null && item.isAlive() && mob.tickCount - orderedAge < MAX_TICKS && mob.hasCoffin() && !mob.isCoffinFull()
                && mob.getTarget() == null && !mob.isSitting();
    }

    @Override
    public void stop() {
        item = null;
        mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (mob.distanceToSqr(item) > COLLECT_RANGE * COLLECT_RANGE) {
            mob.getNavigation().moveTo(item, speed);
            return;
        }

        if (item.hasPickUpDelay()) {
            return;
        }

        var nearby = mob.level().getEntitiesOfClass(
                ItemEntity.class,
                item.getBoundingBox().inflate(COLLECT_RANGE),
                other -> !other.hasPickUpDelay() && other.distanceToSqr(mob) <= COLLECT_RANGE * COLLECT_RANGE * 4
        );
        mob.storeInCoffin(item);
        for (ItemEntity other : nearby) {
            if (other != item) {
                mob.storeInCoffin(other);
            }
        }
        item = null;
    }
}
