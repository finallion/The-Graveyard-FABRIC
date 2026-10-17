package com.lion.graveyard.entities.ai.goals;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.MagpieEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;

/// Only flies to the item. The vanilla `loot()` path does the pickup, as for the fox.
public class PickTreasureGoal extends Goal {
    private static final int RESCAN_INTERVAL = 10;
    private static final double SPEED = 1.2D;
    private static final double GIVE_UP_DISTANCE = 16.0D;
    private static final int MAX_DURATION = 200;

    private final MagpieEntity magpie;
    @Nullable
    private ItemEntity target;
    private int age;

    public PickTreasureGoal(MagpieEntity magpie) {
        this.magpie = magpie;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (this.magpie.isPecking() || !this.magpie.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()) {
            return false;
        }
        World world = this.magpie.getWorld();
        // The vanilla pickup needs the mobGriefing game rule.
        if (!world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING) || this.magpie.getRandom().nextDouble() >= Graveyard.getConfig().number("corvidTheftChance", 0.1D)) {
            return false;
        }
        this.target = this.findTreasure();
        return this.target != null;
    }

    @Nullable
    private ItemEntity findTreasure() {
        List<ItemEntity> items = this.magpie.getWorld().getEntitiesByClass(ItemEntity.class,
                this.magpie.getBoundingBox().expand(8.0D, 4.0D, 8.0D),
                item -> !item.cannotPickup() && this.magpie.canGather(item.getStack()));
        ItemEntity closest = null;
        double closestDistance = Double.MAX_VALUE;
        for (ItemEntity item : items) {
            double distance = this.magpie.squaredDistanceTo(item);
            if (distance < closestDistance) {
                closest = item;
                closestDistance = distance;
            }
        }
        return closest;
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void start() {
        this.age = 0;
        this.magpie.takeOff();
        this.magpie.getNavigation().startMovingTo(this.target, SPEED);
    }

    @Override
    public void tick() {
        this.age++;
        if (this.target != null && this.age % RESCAN_INTERVAL == 0) {
            this.magpie.getNavigation().startMovingTo(this.target, SPEED);
        }
    }

    @Override
    public boolean shouldContinue() {
        return this.target != null && this.target.isAlive()
                && this.magpie.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()
                && this.magpie.squaredDistanceTo(this.target) <= GIVE_UP_DISTANCE * GIVE_UP_DISTANCE
                && this.age <= MAX_DURATION;
    }

    @Override
    public void stop() {
        this.magpie.getNavigation().stop();
        this.target = null;
    }
}
