package com.lion.graveyard.entities.ai.goals;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.MagpieEntity;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.server.level.ServerLevel;

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
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.magpie.isPecking() || !this.magpie.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
            return false;
        }

        // The vanilla pickup needs the mobGriefing game rule.
        if (!(this.magpie.level() instanceof ServerLevel world) || !world.getGameRules().get(GameRules.MOB_GRIEFING) || this.magpie.getRandom().nextDouble() >= Graveyard.getConfig().number("corvidTheftChance", 0.1D)) {
            return false;
        }

        this.target = this.findTreasure(world);
        return this.target != null;
    }

    @Nullable
    private ItemEntity findTreasure(ServerLevel world) {
        List<ItemEntity> items = world.getEntitiesOfClass(
                ItemEntity.class,
                this.magpie.getBoundingBox().inflate(8.0D, 4.0D, 8.0D),
                item -> !item.hasPickUpDelay() && this.magpie.wantsToPickUp(world, item.getItem())
        );
        ItemEntity closest = null;
        double closestDistance = Double.MAX_VALUE;
        for (ItemEntity item : items) {
            double distance = this.magpie.distanceToSqr(item);

            if (distance < closestDistance) {
                closest = item;
                closestDistance = distance;
            }
        }
        return closest;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void start() {
        this.age = 0;
        this.magpie.takeOff();
        this.magpie.getNavigation().moveTo(this.target, SPEED);
    }

    @Override
    public void tick() {
        this.age++;

        if (this.target != null && this.age % RESCAN_INTERVAL == 0) {
            this.magpie.getNavigation().moveTo(this.target, SPEED);
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.target != null && this.target.isAlive()
                && this.magpie.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()
                && this.magpie.distanceToSqr(this.target) <= GIVE_UP_DISTANCE * GIVE_UP_DISTANCE
                && this.age <= MAX_DURATION;
    }

    @Override
    public void stop() {
        this.magpie.getNavigation().stop();
        this.target = null;
    }
}
