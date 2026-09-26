package com.lion.graveyard.entities;

import com.lion.graveyard.entities.ai.goals.FlyOntoPerchGoal;
import com.lion.graveyard.entities.ai.goals.HopAwayGoal;
import com.lion.graveyard.entities.ai.goals.HopGoal;
import com.lion.graveyard.entities.ai.goals.PickTreasureGoal;
import com.lion.graveyard.entities.ai.goals.StartleGoal;
import com.lion.graveyard.init.TGTags;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class MagpieEntity extends CorvidEntity {

    private boolean hopping;

    public MagpieEntity(EntityType<? extends CorvidEntity> type, World world) {
        super(type, world);
        this.setCanPickUpLoot(true);
    }

    @Override
    public Species getSpecies() {
        return Species.MAGPIE;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new StartleGoal(this, 3.5D));
        this.goalSelector.add(2, new HopAwayGoal(this));
        this.goalSelector.add(3, new PickTreasureGoal(this));
        this.goalSelector.add(4, new FlyOntoPerchGoal(this, 1.0D));
        this.goalSelector.add(5, new HopGoal(this));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(7, new LookAroundGoal(this));
    }

    @Override
    public boolean isHopping() {
        return this.hopping;
    }

    public void setHopping(boolean hopping) {
        this.hopping = hopping;
    }

    @Override
    public boolean canGather(ItemStack stack) {
        return stack.isIn(TGTags.MAGPIE_TREASURES) && this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty();
    }

    @Override
    protected void loot(ItemEntity item) {
        ItemStack stack = item.getStack();
        if (this.canGather(stack)) {
            ItemStack taken = stack.split(1);
            this.equipStack(EquipmentSlot.MAINHAND, taken);
            this.updateDropChances(EquipmentSlot.MAINHAND);
            this.sendPickup(item, 1);
            this.triggerItemPickedUpByEntityCriteria(item);
        }
        if (stack.isEmpty()) {
            item.discard();
        } else {
            // The DataTracker syncs only a new instance, not a changed one.
            item.setStack(stack.copy());
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        boolean hurt = super.damage(source, amount);
        if (hurt && !this.getWorld().isClient() && !this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()) {
            this.dropStack(this.getEquippedStack(EquipmentSlot.MAINHAND));
            this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }
        return hurt;
    }
}
