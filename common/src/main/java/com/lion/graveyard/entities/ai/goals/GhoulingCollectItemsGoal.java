package com.lion.graveyard.entities.ai.goals;

import com.lion.graveyard.entities.GhoulingEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

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
        this.setControls(EnumSet.of(Control.MOVE));
    }

    public void collect(ItemEntity item) {
        this.item = item;
        this.orderedAge = mob.age;
    }

    @Override
    public boolean canStart() {
        return isValid();
    }

    @Override
    public boolean shouldContinue() {
        return isValid();
    }

    private boolean isValid() {
        return item != null && item.isAlive() && mob.age - orderedAge < MAX_TICKS && mob.hasCoffin() && !mob.isCoffinFull()
                && mob.getTarget() == null && !mob.isSitting();
    }

    @Override
    public void stop() {
        item = null;
        mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (mob.squaredDistanceTo(item) > COLLECT_RANGE * COLLECT_RANGE) {
            mob.getNavigation().startMovingTo(item, speed);
            return;
        }
        if (item.cannotPickup()) {
            return;
        }
        var nearby = mob.getWorld().getEntitiesByClass(ItemEntity.class, item.getBoundingBox().expand(COLLECT_RANGE),
                other -> !other.cannotPickup() && other.squaredDistanceTo(mob) <= COLLECT_RANGE * COLLECT_RANGE * 4);
        mob.storeInCoffin(item);
        for (ItemEntity other : nearby) {
            if (other != item) {
                mob.storeInCoffin(other);
            }
        }
        item = null;
    }
}
